package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class UsrInfoExt extends InfoMObject {

	@Override
	public boolean initId(List<String> urlIds) {
		usrId = urlIds.get(0);
		return true;
	}

	@InfoMObjAttrDesc(key=true)
	public String usrId;
	
	public String positionId;
	public String locationOfPos; //position÷–µƒŒª÷√
}
