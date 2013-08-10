package com.btiao.infomodel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.utils.BTiaoLog;

public class InfoModelServiceImplNeo4j extends InfoMBaseService {
	static class AttrValue {
		public AttrValue(String name, Object value) {
			this.name = name;
			this.value = value;
		}
		
		public String name;
		public Object value;
	}

	@Override
	public void add(InfoMObject u) throws BTiaoExp {
		Node node = db.createNode();
		setNodeAttrs(node, u);
		addNode2Idx( node, u);
	}

	@Override
	public void get(InfoMObject u) throws BTiaoExp {
		Node n = getNode(u);
		setObjAttrs(u, n);
	}

	@Override
	public void del(InfoMObject u) throws BTiaoExp {
		Node n = getNode(u);
		if (n != null) {
			n.delete();
		}
	}

	@Override
	public void mdf(InfoMObject u) throws BTiaoExp {
		InfoMObject uu = u.clone();
		Node n = getNode(uu);
		setNodeAttrs(n, u);
	}

	@Override
	public void addRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp {
		Node n1 = getNode(o1);
		Node n2 = getNode(o2);
		
		if (n1 == null || n2 == null) {
			String errMsg = "addRel failed!n1="+n1+",n2="+n2+",o1="+o1+",o2="+o2+",r="+r;
			throw new BTiaoExp(ErrCode.INTERNEL_ERROR, new Throwable(errMsg));
		}
		
		if (hasRel(o1, o2, r)) {
			String errMsg = "addRel failed!o1="+o2+",o2="+o2+",r="+r;
			throw new BTiaoExp(ErrCode.NEO4J_ADD_DUPLICATE_REL, new Throwable(errMsg));
		}
		
		n1.createRelationshipTo(n2, new RelTypeNeo4j(r));
	}

	@Override
	public boolean hasRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp {
		Node n1 = getNode(o1);
		Node n2 = getNode(o1);
		
		if (n1 == null || n2 == null) {
			String errMsg = "hasRel failed!n1="+n1+",n2="+n2+",o1="+o1+",o2="+o2+",r="+r;
			throw new BTiaoExp(ErrCode.INTERNEL_ERROR, new Throwable(errMsg));
		}

		Relationship ship = getRelShip(n1, n2, r, o2.getClass());
		return ship != null;
	}

	@Override
	public void delRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp {
		Node n1 = getNode(o1);
		Node n2 = getNode(o1);
		
		Relationship ship = getRelShip(n1, n2, r, o2.getClass());
		if (ship != null) {
			ship.delete();
		}
	}
	
	@Override
	public void begin() {
		Transaction tx = db.beginTx();
		thVar.set(tx);
	}

	@Override
	public void end() {
		Transaction tx = (Transaction)thVar.get();
		tx.finish();
	}
	
	Node getNode(InfoMObject o) throws BTiaoExp {
		Index<Node> idx = getIdx(o);
		AttrValue kv = getKeyInfo(o);
		return idx.get(kv.name, kv.value).getSingle();
	}
	
	private void setNodeAttrs(Node n, InfoMObject o) throws BTiaoExp {
		Field[] fs = o.getClass().getFields();
		for (Field f : fs) {
			InfoMObjAttrDesc an = f.getAnnotation(InfoMObjAttrDesc.class);
			if (!an.store()) {
				continue;
			}
			
			String name = f.getName();
			
			Object value = null;
			try {
				value = f.get(o);
			} catch (Exception e) {
				throw new BTiaoExp(ErrCode.INTERNEL_ERROR, null);
			}
			
			n.setProperty(name, value);
		}
	}
	
	void setObjAttrs(InfoMObject o, Node n) throws BTiaoExp {
		Field[] fs = o.getClass().getFields();
		for (Field f : fs) {
			InfoMObjAttrDesc an = f.getAnnotation(InfoMObjAttrDesc.class);
			if (!an.store()) {
				continue;
			}
			
			String name = f.getName();
			Object v = n.getProperty(name);
			
			try {
				f.set(o, v);
			} catch (Exception e) {
				throw new BTiaoExp(ErrCode.INTERNEL_ERROR, e);
			}
		}
	}
	
	private Index<Node> getIdx(InfoMObject o) {
		return db.index().forNodes(o.getClass().getName());
	}
	
	private void addNode2Idx(Node n, InfoMObject o) throws BTiaoExp {
		Index<Node> idx = getIdx(o);
		
		AttrValue ki = getKeyInfo(o);
		idx.add(n, ki.name, ki.value);
	}
	
	private Map<String,Object> getKeyValueMap(InfoMObject o) throws BTiaoExp {
		Map<String,Object> r = new HashMap<String,Object>();
		
		Field[] fs = o.getClass().getFields();
		for (Field f : fs) {
			InfoMObjAttrDesc an = f.getAnnotation(InfoMObjAttrDesc.class);
			if (!an.key()) {
				continue;
			}
			
			String name = f.getName();
			Object value = null;
			try {
				value = f.get(o);
			} catch (Exception e) {
				throw new BTiaoExp(ErrCode.INTERNEL_ERROR, e);
			}
			
			r.put(name, value);
		}
		
		return r;
	}
	private AttrValue getKeyInfo(InfoMObject o) throws BTiaoExp {
		StringBuilder nameStr = new StringBuilder();
		StringBuilder valueStr = new StringBuilder();
		String sep = ".";
		
		Map<String,Object> key2Value = getKeyValueMap(o);		
		Set<Entry<String,Object>> kvs = key2Value.entrySet();
		if (kvs.size() == 1) {
			for (Entry<String,Object> kv : kvs) {
				return new AttrValue(kv.getKey(), kv.getValue());
			}
		}
		
		for (Entry<String,Object> kv : kvs) {
			String name = kv.getKey();
			name.replace(sep, "\\.");
			
			nameStr.append(name);
			nameStr.append(sep);
			
			String value = kv.getValue().toString();			
			value.replace(sep, "\\.");
			
			valueStr.append(value);
			valueStr.append(sep);
		}
		
		return new AttrValue(nameStr.toString(), valueStr.toString());
	}
	
	/**
	 * if there is a node with which node 'n' has the specified ship 'r',<br>
	 * then, return the node. otherwise return null
	 * @param n
	 * @param r
	 * @return
	 * @throws BTiaoExp 
	 */
	private Relationship getRelShip(Node n1, Node n2, RelType r, Class<?> n2Clz) throws BTiaoExp {
		Iterable<Relationship> ships = n1.getRelationships(Direction.OUTGOING);
		if (ships != null) {
			Iterator<Relationship> it = ships.iterator();
			while (it.hasNext()) {
				Relationship ship = it.next();
				if (!ship.isType(new RelTypeNeo4j(r))) {
					continue;
				}

				Node relNode = ship.getEndNode();
				if (relNode == null) {
					continue;
				}
				
				if (isSameNode(relNode, n2, n2Clz)) {
					return ship;
				}
			}
		}
		
		return null;
	}
	
	private Collection<String> getKeys(Class<?> clz) {
		List<String> keys = new ArrayList<String>();
		
		Field[] fs = clz.getFields();
		for (Field f : fs) {
			InfoMObjAttrDesc an = f.getAnnotation(InfoMObjAttrDesc.class);
			if (!an.key()) {
				continue;
			}
			
			String name = f.getName();
			keys.add(name);
		}
		
		return keys;
	}
	
	private boolean isSameNode(Node n1, Node n2, Class<?> clz) throws BTiaoExp {
		Collection<String> ks = getKeys(clz);
		for (String k : ks) {
			Object v1 = n1.getProperty(k);
			Object v2 = n2.getProperty(k);
			if (!v1.equals(v2)) {
				return false;
			}
		}
		
		return ks.size() == 0 ? n1.equals(n2) : true;
	}
	
	Logger log = BTiaoLog.get();
	
	GraphDatabaseService db;

	ThreadLocal<Transaction> thVar = new ThreadLocal<Transaction>();
}
