package com.btiao.common.service;

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
	
	public synchronized InfoMObject getInfoObject(
			Class<?extends InfoMObject>infoClz, List<String> urlIds) throws BTiaoExp {
		InfoMObject info = null;
		try {
			info = infoClz.newInstance();
		} catch (Exception e) {
			String extMsg = "infoClz.newInstance failed,class="+infoClz.getName()+",id="+getUrlIdsStr(urlIds);
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, e, extMsg);
		}
		
		info.initId(urlIds);
		if (InfoMBaseService.instance().get(info)) {
			return info;
		} else {
			String errMsg = "fetch from db error,class="+infoClz.getName()+",id="+getUrlIdsStr(urlIds);
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, new Throwable(errMsg));
		}
	}
	
	public synchronized void delInfoObject(
			Class<?extends InfoMObject> infoClz, List<String> urlIds) throws BTiaoExp {
		InfoMObject info = null;
		try {
			info = infoClz.newInstance();
			info.initId(urlIds);
		} catch (Exception e) {
			String extMsg = "infoClz.newInstance failed,class="+infoClz.getName()+",id="+getUrlIdsStr(urlIds);
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, e, extMsg);
		}
		
		InfoMBaseService.instance().begin();
		try {
			InfoMBaseService.instance().del(info);
			
			InfoMBaseService.instance().success();
		} catch (BTiaoExp e) {
			InfoMBaseService.instance().failed();
			throw e;
		} finally {
			InfoMBaseService.instance().finish();
		}
	}
	
	public void addInfoObject(String relName, InfoMObject from, InfoMObject info) throws BTiaoExp {
		base.begin();
		try {
			InfoMBaseService.instance().add(info);
			base.addRel(from, info, new RelType(relName));
			
			base.success();
		} catch (Throwable e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
	
	public void addInfoObject(InfoMObject info) throws BTiaoExp {
		base.begin();
		try {
			InfoMBaseService.instance().add(info);
			
			base.success();
		} catch (Throwable e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
	}
	
	public synchronized void updateInfoObject(
			InfoMObject info, Collection<String> attrs) throws BTiaoExp {
		InfoMBaseService.instance().begin();
		try {
			InfoMObject newObj = info.clone();
			if (!InfoMBaseService.instance().get(newObj)) {
				throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, new Throwable(newObj.toString()));
			}
			
			newObj.update(info, attrs);
			InfoMBaseService.instance().mdf(newObj);
			
			InfoMBaseService.instance().success();
		} catch (BTiaoExp e) {
			InfoMBaseService.instance().failed();
			throw e;
		} finally {
			InfoMBaseService.instance().finish();
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
