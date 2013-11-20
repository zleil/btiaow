package com.btiao.product.domain;

import java.util.List;

import com.btiao.base.utils.BTiaoLog;
import com.btiao.infomodel.InfoMObject;

public class AccessRight extends InfoMObject {
	static public class Right{
		static public int ALLOW = 0;
		static public int NOTALLOW = 1;
		
		static public int DEFAULT = ALLOW;
	}
	static public enum Action {
		PUT,POST,DELETE,GUT,GETALL;
	}

	@Override
	public boolean initId(List<String> urlIds) {
		return true;
	}
	
	public Action getAction() {
		String[] splits = name.split("_");
		return Action.valueOf(splits[0]);
	}
	
	public Class<?extends InfoMObject> getResClass() {
		String[] splits = name.split("_");
		String className = "com.btiao.product.domain." + splits[1];
		try {
			return (Class<? extends InfoMObject>) Class.forName(className);
		} catch (Exception e) {
			Logger log = BTiaoLog.get();
			BTiaoLog.logExp(, e, "getResClass from "+this.name+" failed!");
			return null;
		}
	}

	public String name;
	
	public String value;
}
