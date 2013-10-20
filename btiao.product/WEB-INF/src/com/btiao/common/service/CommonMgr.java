package com.btiao.common.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.infomodel.InfoMBaseService;
import com.btiao.infomodel.InfoMObject;
import com.btiao.infomodel.RelType;
import com.btiao.product.restlet.RelName;

public class CommonMgr {
	static public CommonMgr instance() {
		if (inst == null) {
			inst = new CommonMgr();
		}
		return inst;
	}
	
	static private CommonMgr inst;
	
	public InfoMObject getInfoObject(
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
	
	public List<InfoMObject> timeSeqGet(
			InfoMObject obj, int num) throws BTiaoExp {
		List<InfoMObject> ret = new ArrayList<InfoMObject>();
		
		while (num-- > 0) {
			InfoMObject o = base.getFirstRelObj(
					obj, new RelType(RelName.infoTimeSeq), obj.getClass(), true);
			if (o == null) {
				break;
			}
			
			ret.add(o);
			obj = o;
		}
		
		return ret;
	}
	
	public InfoMObject getFirstRelObj(
			InfoMObject obj, 
			String relName, 
			Class<?extends InfoMObject> relObjClz, 
			boolean isOut) throws BTiaoExp {
		return base.getFirstRelObj(obj, new RelType(relName), relObjClz, isOut);
	}
	
	public void delInfoObject(InfoMObject info) throws BTiaoExp {
		base.begin();
		try {
			base.del(info);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
	
	public void delTimeSeqInfoObject(String relName,
			InfoMObject from, InfoMObject to) throws BTiaoExp {
		base.begin();
		try {
			//first, delete relation of to
			if (base.hasRel(from, to, new RelType(relName))) {
				InfoMObject next = base.getFirstRelObj(to, new RelType(RelName.infoTimeSeq), to.getClass(), true);
				
				if (next != null) {
					base.delRel(from, to, new RelType(relName));
					base.delRel(to, next, new RelType(RelName.infoTimeSeq));
					
					base.addRel(from, next, new RelType(relName));
				} else {
					base.delRel(from, to, new RelType(relName));
				}
			} else {
				InfoMObject next = base.getFirstRelObj(to, new RelType(RelName.infoTimeSeq), to.getClass(), true);
				InfoMObject pre = base.getFirstRelObj(to, new RelType(RelName.infoTimeSeq), to.getClass(), true);
				
				if (next != null) {
					base.delRel(to, next, new RelType(RelName.infoTimeSeq));
					base.delRel(pre, to, new RelType(RelName.infoTimeSeq));
					
					base.addRel(pre, next, new RelType(RelName.infoTimeSeq));
				} else {
					base.delRel(pre, to, new RelType(RelName.infoTimeSeq));
				}
			}
			
			//second delete to
			base.del(to);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
	
	public void addTimeSeqInfoObject(
			String relName, 
			InfoMObject from, 
			InfoMObject info) throws BTiaoExp {
		base.begin();
		try {
			base.add(info);
			InfoMObject firstInfo = base.getFirstRelObj(from, 
					new RelType(relName), info.getClass(), true);
			if (firstInfo == null) {
				base.addRel(from, info, new RelType(relName));
			} else {
				InfoMObject lastInfo = firstInfo;
				do {
					InfoMObject next = base.getFirstRelObj(
							lastInfo, 
							new RelType(RelName.infoTimeSeq), 
							lastInfo.getClass(), 
							true);
					if (next == null) break;
					
					lastInfo = next;
				} while (true);
				
				base.addRel(lastInfo, info, new RelType(RelName.infoTimeSeq));
			}
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
		
	public void addInfoObject(InfoMObject info) throws BTiaoExp {
		base.begin();
		try {
			base.add(info);
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
	
	public void updateInfoObject(
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
	
	private InfoMBaseService base = InfoMBaseService.instance();
}
