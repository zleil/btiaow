package com.btiao.infomodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;

public class InfoMServiceImplNeo4j extends InfoMService {

	@Override
	public Collection<?> getRelObject(InfoMObject o1, RelType r, Class<?> o2Type)  throws BTiaoExp {
		List<InfoMObject> ret = new ArrayList<InfoMObject>();
		
		Node n1 = base.getNodeFromIdx(o1);
		Iterable<Relationship> relIt = n1.getRelationships();
		if (relIt != null) {
			Iterator<Relationship> it = relIt.iterator();
			while (it.hasNext()) {
				Relationship ship = it.next();
				if (!ship.isType(new RelTypeNeo4j(r))) {
					continue;
				}
				
				try {
					Node n2 = ship.getEndNode();
					InfoMObject o2 = (InfoMObject)o2Type.newInstance();
					base.setObjAttrs(o2, n2);
					
					ret.add(o2);
				} catch (Exception e) {
					throw new BTiaoExp(ErrCode.INTERNEL_ERROR, e);
				}
			}
		}
		
		return ret;
	}

	@Override
	public void delBFromeA(InfoMObject a, InfoMObject b, RelType r) throws BTiaoExp{
		base.delRel(a, b, r);
		base.del(b);
	}

	@Override
	public void begin() {
		base.begin();
	}

	@Override
	public void finish() {
		base.finish();
	}
	
	@Override
	public void success() {
		base.success();
	}
	
	@Override
	public void failed() {
		base.failed();
	}

	private InfoMBaseServiceImplNeo4j base = 
			(InfoMBaseServiceImplNeo4j)InfoMBaseService.instance();
}
