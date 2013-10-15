package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class BlockInfo extends InfoMObject {
	@Override
	public boolean initId(List<String> urlIds) {
		if (urlIds.size() != 2) {
			return false;
		}
		this.posId = urlIds.get(0);
		this.id = urlIds.get(1);
		return true;
	}
	
	@InfoMObjAttrDesc(key=true)
	public String id = "";
	
	public String type = "";
	public String posId = "";
	
	public String desc = "";
	public int price;
	public int oldPrice;
	public String imageUrl = "";
	
	public int evaluteValue;
}
