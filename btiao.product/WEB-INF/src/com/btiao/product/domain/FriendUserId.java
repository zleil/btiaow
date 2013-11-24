package com.btiao.product.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class FriendUserId extends InfoMObject {

	@Override
	public boolean initId(List<String> urlIds) {
		this.friendId = urlIds.get(0);
		return true;
	}

	@InfoMObjAttrDesc(key=true)
	public String friendId = "";
	
	public String deviceId = "";
}
