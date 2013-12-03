package com.btiao.common.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btiao.base.model.BTiaoRoot;
import com.btiao.base.model.BTiaoUser;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.AccessRight;
import com.btiao.product.domain.AccessRight.Action;
import com.btiao.product.domain.AccessRight.Right;
import com.btiao.product.domain.Position;
import com.btiao.product.restlet.RelName;

public class RightMgr {
	static public class AllObjRight {
		public Map<Target,AccessRight> ownerRight = new HashMap<Target,AccessRight>();
		public Map<Target,AccessRight> otherRight = new HashMap<Target,AccessRight>();
		public Map<String,Group> groupInfos = new HashMap<String,Group>(); //gid->group
		public Map<Target,List<AccessRight>> groupRight = new HashMap<Target,List<AccessRight>>();
	}
	static public class Group {
		public Group(String gId, Collection<String> allUid) {
			for (String uId : allUid) {
				members.put(uId, true);
			}
		}
		public boolean isMember(String uId) {
			return members.get(uId) != null;
		}
		
		public String gId;
		public Map<String,Boolean> members = new HashMap<String,Boolean>();
	}
	static public class Target {
		public Action act;
		public String relName;
		
		public Target(Action act, String relName) {
			this.act = act;
			this.relName = relName;
		}
		public int hashCode() {
			return act.hashCode() + relName.hashCode();
		}
		public boolean equals(Object oo) {
			if (oo.getClass() != this.getClass()) return false;
			Target o = (Target)oo;
			return act.equals(o.act) && o.relName.equals(this.relName);
		}
		public String toString() {
			return act.name() + "," + relName;
		}
	}
	
	static public RightMgr instance() {
		if (inst == null) {
			inst = new RightMgr();
		}
		return inst;
	}
	static private RightMgr inst;
	
	public boolean canDo(InfoMObject obj, Action act, String relName, String uId) {
		AllObjRight rights = getAllRight(obj);
		Target target = new Target(act, relName);
		if (isOwner(obj, uId)) {
			return checkOwner(rights, target);
		}
		
		List<AccessRight> allGroupRight = rights.groupRight.get(target);
		if (allGroupRight == null || allGroupRight.size() == 0) {
			return checkOther(rights, target);
		}
		
		boolean inGroup = false;
		int lowestRight = Right.NOTALLOW;
		for (AccessRight right : allGroupRight) {
			String gId = right.gId;
			Group group = rights.groupInfos.get(gId);
			if (group != null && group.isMember(uId)) {
				if (lowestRight>right.value) {
					lowestRight = right.value;
					inGroup = true;
				}
			}
		}
		if (inGroup) {
			return lowestRight == Right.ALLOW;
		} else {
			return checkOther(rights, target);
		}
	}
	
	private boolean checkOther(AllObjRight rights, Target target) {
		//check other right
		AccessRight right = rights.otherRight.get(target);
		if (right == null) {
			//default owner right
			if (target.act == Action.GET || target.act == Action.GETALL) {
				return true; //default get is allow
			}
			return false;
		} else {
			return right.value == Right.ALLOW;
		}
	}
	private boolean checkOwner(AllObjRight rights, Target target) {
		//check other right
		AccessRight right = rights.ownerRight.get(target);
		if (right == null) {
			//default owner right is allow
			return true;
		} else {
			return right.value == Right.ALLOW;
		}
	}
	
	private AllObjRight getAllRight(InfoMObject obj) {
		//TODO
		AllObjRight r = new AllObjRight();
		if (obj.getClass() == BTiaoUser.class) {
			Target add = new Target(Action.PUT, RelName.order_of_user);
			Target del = new Target(Action.DELETE, RelName.order_of_user);
			
			AccessRight rightAdd = new AccessRight(add.act, add.relName,AccessRight.GroupNameCONST.gIdOther);
			AccessRight rightDel = new AccessRight(del.act, del.relName,AccessRight.GroupNameCONST.gIdOther);
					
			r.otherRight.put(add, rightAdd);
			r.otherRight.put(del, rightDel);
		} else if (obj.getClass() == Position.class){
			Target add = new Target(Action.PUT, RelName.order_of_position);
			Target del = new Target(Action.DELETE, RelName.order_of_position);
			
			AccessRight rightAdd = new AccessRight(add.act, add.relName,AccessRight.GroupNameCONST.gIdOther);
			AccessRight rightDel = new AccessRight(del.act, del.relName,AccessRight.GroupNameCONST.gIdOther);
					
			r.otherRight.put(add, rightAdd);
			r.otherRight.put(del, rightDel);
		} else if (obj.getClass() == BTiaoRoot.class) {
			
		}
		return r;
	}
	
	private boolean isOwner(InfoMObject obj, String uId) {
		//TODO
		//1. …Ë÷√objµƒownerUser in every object in base project.
		//2. 
		if (uId.equals("_mgr0")) {
			return true;
		}
		if (obj.ownerUser.equals(uId)) {
			return true;
		}
		
		return false;
	}
	
	private RightMgr() {}
}
