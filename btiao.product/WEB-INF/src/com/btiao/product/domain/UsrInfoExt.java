package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class UsrInfoExt extends InfoMObject {
	static public final String FRIEND_UID_ATTR = "friendUid";

	@Override
	public boolean initId(List<String> urlIds) {
		usrId = urlIds.get(0);
		return true;
	}

	@InfoMObjAttrDesc(key=true)
	public String usrId;
	
	public String friendUid;
	
	public String passwd = "";
	
	public String positionId = "1000001";
	
	public String posToLive = "";
	public String locationOfPos = ""; //position�е�λ��
}
