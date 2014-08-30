package com.btiao.tzsc.restful;

import com.btiao.tzsc.service.SessionMgr;

public class TZSCCookieInfo {
	public String usrId;
	public String token;
	public long areaId = 0;
	
	public boolean isValidSession() {
		if (usrId == null || token == null || areaId == 0) {
			return false;
		}
		
		return SessionMgr.instance(areaId).isOnlineUser(usrId, token);
	}
}
