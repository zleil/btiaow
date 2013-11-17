package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class BlockInfo extends InfoMObject {
	static public class State {
		static public int VALID = 0;
		static public int HISTORY = 1;
	}
	
	@Override
	public boolean initId(List<String> urlIds) {
		this.id = urlIds.get(0);
		return true;
	}
	
	@InfoMObjAttrDesc(key=true)
	public String id = "";
	
	public String type = "product";
	public String posId = "";
	
	public int state;
	
	public String desc = "";
	
	public long createTime = System.currentTimeMillis();
	
	public int price;
	public int oldPrice;
	
	public String imgUrl = "";
	
	public int evaluteValue;
}
