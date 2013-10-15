package com.btiao.user.domain;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class BTiaoUserLogInfo extends InfoMObject {
	public BTiaoUserLogInfo(String uId, String token) {
		this.uId = uId;
		this.token = token;
	}
	
	public BTiaoUserLogInfo() {}
	
	@Override
	public boolean initId(List<String> urlIds) {
		if (urlIds.size() != 2) {
			return false;
		}
		
		this.uId = urlIds.get(0);
		this.token = urlIds.get(1);
		return true;
	}
	
	@InfoMObjAttrDesc(key=true)
	public String uId;
	
	@InfoMObjAttrDesc(key=true)
	public String token;
	
	public long startTime;
	
	public long endTime;
}
