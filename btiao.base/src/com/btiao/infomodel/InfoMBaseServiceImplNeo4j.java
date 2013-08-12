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

public class InfoMBaseServiceImplNeo4j extends InfoMBaseService {
	static public void main(String[] args) throws BTiaoExp {
		class TestObj extends InfoMObject {
			TestObj(String id, String desc, int age) {
				this.id = id;
				this.desc = desc;
				this.age = age;
			}
			@InfoMObjAttrDesc(key=true)
			public String id;
			public String desc;
			public int age;
		}
		class TestObj2 extends InfoMObject {
			TestObj2(String id, int id2, String desc, int age) {
				this.id = id;
				this.id2 = id2;
				this.desc = desc;
				this.age = age;
			}
			@InfoMObjAttrDesc(key=true)
			public String id;
			@InfoMObjAttrDesc(key=true)
			public int id2;
			public String desc;
			public int age;
		}
		Neo4jMgr.instance().clearDB();
		Neo4jMgr.instance().init();
		
		TestObj u = new TestObj("zleil", "zhanglei", 31);
		InfoMBaseService base = InfoMBaseService.instance();
		
		// check add node
		base.begin();
		try {
			base.add(u);
			
			base.success();
		} finally {
			base.finish();
		}
		
		TestObj uu = new TestObj("zleil", null, 0);
		base.get(uu);
		assert(uu.age == u.age);
		assert(uu.desc.equals(u.desc));
		
		base.begin();
		try {
			base.del(u);
			
			base.success();
		} finally {
			base.finish();
		}
		
		uu = new TestObj("zleil", null, 0);
		assert(!base.get(uu));
		
		base.begin();
		try {
			base.add(u);
			
			base.success();
		} finally {
			base.finish();
		}
		
		uu = new TestObj("zleil", null, 0);
		base.get(uu);
		assert(uu.age == u.age);
		assert(uu.desc.equals(u.desc));
		
		//check mdf node
		u.age = 32;
		u.desc = "new desc";

		base.begin();
		try {
			base.mdf(u);
			
			base.success();
		} finally {
			base.finish();
		}
		
		uu = new TestObj("zleil", null, 0);
		base.get(uu);
		assert(uu.age == u.age);
		assert(uu.desc.equals(u.desc));
		
		//check addRel
		TestObj2 u2 = new TestObj2("zl", 3, "fffx", 55);
		RelType r = new RelType("employ");
		base.begin();
		try {
			base.add(u2);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			assert(false);			
		} finally {
			base.finish();
		}
		
		TestObj2 uu2 = new TestObj2("zl", 3, "", 0);
		assert(base.get(uu2));
		assert(uu2.age == u2.age);
		assert(uu2.desc.equals(u2.desc));
		
		base.begin();
		try {
			base.addRel(u, u2, r);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			assert(false);
		} finally {
			base.finish();
		}
		
		assert(base.hasRel(u, u2, new RelType("employ")));
		assert(!base.hasRel(u, u2, new RelType("employxx")));
		
		//check delRel
		base.begin();
		try {
			base.delRel(u, u2, r);
			base.delRel(u, u2, r); //del not exist one should success
			//assert(!base.hasRel(u, u2, new RelType("employ")));
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			assert(false);
		} finally {
			base.finish();
		}
		assert(!base.hasRel(u, u2, new RelType("employ")));
		
		//check del node
		base.begin();
		try {
			base.del(u2);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			assert(false);	
		} finally {
			base.finish();
		}
		uu2 = new TestObj2("zl", 3, "", 0);
		assert(!base.get(uu2));
		
		base.begin();
		try {
			base.del(u);
			
			base.success();
		} finally {
			base.finish();
		}
		
		uu = new TestObj("zleil", null, 0);
		assert(!base.get(uu));
	
		try {
			assert(false);
			System.out.println("please use VM argument -ea");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}
	
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
		Node n = getNodeFromIdx(u);
		if (n != null) {
			throw new BTiaoExp(ErrCode.ADD_DUP_OBJ_TO_INFO_MODEL, null);
		}
		
		Node node = db.createNode();
		setNodeAttrs(node, u);
		addNode2Idx( node, u);
	}

	@Override
	public boolean get(InfoMObject u) throws BTiaoExp {
		Node n = getNodeFromIdx(u);
		if (n == null) {
			return false;
		}
		
		setObjAttrs(u, n);
		return true;
	}

	@Override
	public void del(InfoMObject u) throws BTiaoExp {
		Node n = getNodeFromIdx(u);
		if (n != null) {
			n.delete();
		}
	}

	@Override
	public void mdf(InfoMObject u) throws BTiaoExp {
		Node n = getNodeFromIdx(u);
		if (n == null) {
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, null);
		}
		
		setNodeAttrs(n, u);
	}

	@Override
	public void addRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp {
		Node n1 = getNodeFromIdx(o1);
		Node n2 = getNodeFromIdx(o2);
		
		if (n1 == null || n2 == null) {
			String errMsg = "addRel failed!n1="+n1+",n2="+n2+",o1="+o1+",o2="+o2+",r="+r;
			throw new BTiaoExp(ErrCode.INTERNEL_ERROR, new Throwable(errMsg));
		}
		
		if (getRelShip(n1, n2, r, o2.getClass()) != null) {
			String errMsg = "addRel failed!o1="+o2+",o2="+o2+",r="+r;
			throw new BTiaoExp(ErrCode.ADD_DUP_REL_TO_INFO_MODEL, new Throwable(errMsg));
		}
		
		RelTypeNeo4j rel = new RelTypeNeo4j(r);
		n1.createRelationshipTo(n2, rel);
	}

	@Override
	public boolean hasRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp {
		Node n1 = getNodeFromIdx(o1);
		Node n2 = getNodeFromIdx(o2);
		
		if (n1 == null || n2 == null || r == null) {
			String errMsg = "hasRel failed!n1="+n1+",n2="+n2+",o1="+o1+",o2="+o2+",r="+r;
			throw new BTiaoExp(ErrCode.INTERNEL_ERROR, new Throwable(errMsg));
		}

		Relationship ship = getRelShip(n1, n2, r, o2.getClass());
		return ship != null;
	}

	@Override
	public void delRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp {
		Node n1 = getNodeFromIdx(o1);
		Node n2 = getNodeFromIdx(o2);
		
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
	public void finish() {
		Transaction tx = (Transaction)thVar.get();
		tx.finish();
	}
	
	@Override
	public void success() {
		Transaction tx = (Transaction)thVar.get();
		tx.success();
	}
	
	@Override
	public void failed() {
		;
	}
	
	Node getNodeFromIdx(InfoMObject o) throws BTiaoExp {
		Index<Node> idx = getIdx(o);
		AttrValue kv = getKeyInfo(o);
		return idx.get(kv.name, kv.value).getSingle();
	}
	
	private void setNodeAttrs(Node n, InfoMObject o) throws BTiaoExp {
		Field[] fs = o.getClass().getFields();
		for (Field f : fs) {
			InfoMObjAttrDesc an = f.getAnnotation(InfoMObjAttrDesc.class);
			if (an != null && !an.store()) {
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
			if (an != null && !an.store()) {
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
		String idxName = o.getClass().getName();
		return db.index().forNodes(idxName);
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
			if (an == null || !an.key()) {
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
		Iterable<Relationship> ships = n1.getRelationships(new RelTypeNeo4j(r), Direction.OUTGOING);
		Iterator<Relationship> it = ships.iterator();
		while (it.hasNext()) {
			Relationship ship = it.next();
			Node relNode = ship.getEndNode();
			
			if (isSameNode(relNode, n2, n2Clz)) {
				return ship;
			}
		}
		
		return null;
	}
	
	private Collection<String> getKeys(Class<?> clz) {
		List<String> keys = new ArrayList<String>();
		
		Field[] fs = clz.getFields();
		for (Field f : fs) {
			InfoMObjAttrDesc an = f.getAnnotation(InfoMObjAttrDesc.class);
			if (an == null || !an.key()) {
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
	
	GraphDatabaseService db = Neo4jMgr.instance().db();

	ThreadLocal<Transaction> thVar = new ThreadLocal<Transaction>();
}