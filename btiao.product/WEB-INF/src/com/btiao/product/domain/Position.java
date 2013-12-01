package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class Position extends InfoMObject {
	static public String FUID_NAME = "fuid";
	
	@Override
	public boolean initId(List<String> urlIds) {
		if (urlIds.size() != 1) {
			return false;
		}
		this.id = urlIds.get(0);
		return true;
	}
	
	@InfoMObjAttrDesc(key=true)
	public String id = "";
	
	public String pid = "";
	
	public String name = "";
	public String desc = "";
	public String imgUrl = "";
	
	public String fuid = "";
}
