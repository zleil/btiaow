package com.btiao.user.domain;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class BTiaoUserLogInfo extends InfoMObject {
	public BTiaoUserLogInfo(String uId, String token) {
		this.uId = uId;
		this.token = token;
	}
	
	public BTiaoUserLogInfo() {}
	
	@InfoMObjAttrDesc(key=true)
	public String uId;
	
	@InfoMObjAttrDesc(key=true)
	public String token;
	
	public long startTime;
	
	public long endTime;
}
