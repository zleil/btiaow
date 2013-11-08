package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class BTiaoId extends InfoMObject {
	public String nextValue() {
		try {
			long v = Long.parseLong(nextValue);
			++v;
			nextValue = Long.toString(v);
		} catch (Exception e) {}
		
		return nextValue;
	}

	@Override
	public boolean initId(List<String> urlIds) {
		// TODO Auto-generated method stub
		return false;
	}

	@InfoMObjAttrDesc(key=true)
	public String idName = "";
	
	public String nextValue = "1000001";
}
