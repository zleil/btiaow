package com.btiao.common.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.infomodel.InfoMBaseService;
import com.btiao.infomodel.InfoMObject;
import com.btiao.infomodel.RelType;

public class CommonMgr {
	static public CommonMgr instance() {
		if (inst == null) {
			inst = new CommonMgr();
		}
		return inst;
	}
	
	static private CommonMgr inst;
	
	public InfoMObject getObject(
			Class<?extends InfoMObject>infoClz, List<String> urlIds) throws BTiaoExp {
		InfoMObject info = null;
		try {
			info = infoClz.newInstance();
		} catch (Exception e) {
			String extMsg = "infoClz.newInstance failed,class="+infoClz.getName()+",id="+getUrlIdsStr(urlIds);
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, e, extMsg);
		}
		
		info.initId(urlIds);
		if (base.get(info)) {
			return info;
		} else {
			String errMsg = "fetch from db error,class="+infoClz.getName()+",id="+getUrlIdsStr(urlIds);
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, new Throwable(errMsg));
		}
	}
	

	public void updateObject(
			InfoMObject info, Collection<String> attrs) throws BTiaoExp {
		base.begin();
		try {
			InfoMObject newObj = info.clone();
			if (!base.get(newObj)) {
				throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, new Throwable(newObj.toString()));
			}
			
			newObj.update(info, attrs);
			base.mdf(newObj);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}

	public void addObjectRightAndDownRel(
			String rightRelName, 
			InfoMObject from, 
			InfoMObject to,
			String downRelName, 
			boolean justAddRel) throws BTiaoExp {
		try {
			ParallelMgr.ModelInfo m = new ParallelMgr.ModelInfo(from, to.getClass(),
					rightRelName, downRelName);
			if (!ParallelMgr.instance().writeAccess(m, 3000)) {
				throw new BTiaoExp(ErrCode.TIME_OUT, "addObjectRightAndDownRel time,m="+m);
			}
			
			_addObjectRightAndDownRel(rightRelName,from,to,downRelName,justAddRel);
		} finally {
			ParallelMgr.instance().release();
		}
	}
	private void _addObjectRightAndDownRel(
			String rightRelName, 
			InfoMObject from, 
			InfoMObject to,
			String downRelName, 
			boolean justAddRel) throws BTiaoExp {
		base.begin();
		try {
			InfoMObject firstInfo = base.getFirstRelObj(from, 
					new RelType(rightRelName), to.getClass(), true);
			
			if (!justAddRel) base.add(to);
			base.addRel(from, to, new RelType(rightRelName));
			
			if (firstInfo != null) {
				base.delRel(from, firstInfo, new RelType(rightRelName));
				base.addRel(to, firstInfo, new RelType(downRelName));
			}
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
	
	public void delObjectRightAndDownRel(String rightRelName, 
			InfoMObject from, 
			InfoMObject to,
			String downRelName, 
			boolean justDelRel) throws BTiaoExp {
		try {
			ParallelMgr.ModelInfo m = new ParallelMgr.ModelInfo(from, to.getClass(),
					rightRelName, downRelName);
			if (!ParallelMgr.instance().writeAccess(m, 3000)) {
				throw new BTiaoExp(ErrCode.TIME_OUT, "delObjectRightAndDownRel time,m="+m);
			}
			
			_delObjectRightAndDownRel(rightRelName,from,to,downRelName,justDelRel);
		} finally {
			ParallelMgr.instance().release();
		}
	}
	
	private void _delObjectRightAndDownRel(String rightRelName, 
			InfoMObject from, 
			InfoMObject to,
			String downRelName, 
			boolean justDelRel) throws BTiaoExp {
		base.begin();
		try {
			boolean isFirst = base.hasRel(from, to, new RelType(rightRelName));
			
			InfoMObject nextObj = base.getFirstRelObj(to, 
					new RelType(downRelName), to.getClass(), true);
			
			if (isFirst) {
				base.delRel(from, to, new RelType(rightRelName));
				if (nextObj != null) {
					base.delRel(to, nextObj, new RelType(downRelName));
					base.addRel(from, nextObj, new RelType(rightRelName));
				}
			} else {
				InfoMObject upperObj = base.getFirstRelObj(to, 
						new RelType(downRelName), to.getClass(), false);
				base.delRel(upperObj, to, new RelType(downRelName));
				if (nextObj != null) {
					base.delRel(to, nextObj, new RelType(downRelName));
					base.addRel(upperObj, nextObj, new RelType(downRelName));
				}
			}
			
			if (!justDelRel) base.del(to);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
	
	public List<InfoMObject> getAllObjRightAndDownRel(InfoMObject from, 
			Class<?extends InfoMObject> toClass, String rightRelName, 
			InfoMObject lastObj, String downRelName, int num) throws BTiaoExp {
		try {
			ParallelMgr.ModelInfo m = new ParallelMgr.ModelInfo(from, toClass,
					rightRelName, downRelName);
			if (!ParallelMgr.instance().readAccess(m, 3000)) {
				throw new BTiaoExp(ErrCode.TIME_OUT, "getAllObjRightAndDownRel time,m="+m);
			}
			
			return _getAllObjRightAndDownRel(from,toClass,rightRelName,lastObj,downRelName, num);
		} finally {
			ParallelMgr.instance().release();
		}
	}
	
	private List<InfoMObject> _getAllObjRightAndDownRel(InfoMObject from, 
			Class<?extends InfoMObject> toClass, String rightRelName, 
			InfoMObject lastObj, String downRelName, int num) throws BTiaoExp {
		if (lastObj == null) {
			InfoMObject firstObj = 
					base.getFirstRelObj(from, new RelType(rightRelName), toClass, true);
			
			if (firstObj == null) {
				return new ArrayList<InfoMObject>();
			} else {
				List<InfoMObject> ret = new ArrayList<InfoMObject>();
				ret.add(firstObj);
				if (num > 1) {
					List<InfoMObject> otherRet = seqGet(downRelName, firstObj, num-1);
					ret.addAll(otherRet);
				}
				return ret;
			}
		} else {
			return seqGet(downRelName, lastObj, num);
		}
	}
	
	private List<InfoMObject> seqGet(
			String relName, InfoMObject obj, int num) throws BTiaoExp {
		List<InfoMObject> ret = new ArrayList<InfoMObject>();
		
		while (num-- > 0) {
			InfoMObject o = base.getFirstRelObj(
					obj, new RelType(relName), obj.getClass(), true);
			if (o == null) {
				break;
			}
			
			ret.add(o);
			obj = o;
		}
		
		return ret;
	}
	
	private String getUrlIdsStr(List<String> urlIds) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String id : urlIds) {
			sb.append(id);
			sb.append(",");
		}
		sb.append("}");
		return sb.toString();
	}
	
	private CommonMgr() {}
	
	public InfoMBaseService base = InfoMBaseService.instance();
}
