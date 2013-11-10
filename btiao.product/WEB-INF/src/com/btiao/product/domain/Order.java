package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class Order extends InfoMObject {

	@Override
	public boolean initId(List<String> urlIds) {
		this.orderId = urlIds.get(0);
		return true;
	}

	public String posId = "";
	
	@InfoMObjAttrDesc(key=true)
	public String orderId = "";
	
	public String productId = "";
	public String productDesc = "";
	public int productNum;
	
	public int totalPrice;
	
	public String fromUser = "";
	
	public boolean discard = false;
}
