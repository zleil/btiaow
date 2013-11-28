package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class Order extends InfoMObject {
	static public class State {
		static public final int VALID = 0;
		static public final int DISCARD = 1;
		static public final int FINISHED = 2;
	}

	@Override
	public boolean initId(List<String> urlIds) {
		this.id = urlIds.get(0);
		return true;
	}

	public String posId = "";
	
	@InfoMObjAttrDesc(key=true)
	public String id = "";
	
	public String productId = "";
	public String productDesc = "";
	public int productNum;
	
	public int totalPrice;
	
	public String fromUser = "";
	
	public int state;
	
	public String orderDst = "";
	
	public long createTime = System.currentTimeMillis(); //毫秒数，相对[UTC]1970.1.1 0点0分
	
	public String mark = "";
}
