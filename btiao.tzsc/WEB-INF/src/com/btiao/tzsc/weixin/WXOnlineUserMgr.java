package com.btiao.tzsc.weixin;

import java.util.HashMap;
import java.util.Map;

public class WXOnlineUserMgr {
	static private Map<Long,WXOnlineUserMgr> insts = new HashMap<Long,WXOnlineUserMgr>();
	
	static public synchronized WXOnlineUserMgr instance(long areaId) {
		WXOnlineUserMgr inst = insts.get(areaId);
		if (inst == null) {
			inst = new WXOnlineUserMgr(areaId);
			insts.put(areaId, inst);
			return inst;
		}
		
		return inst;
	}
	
	public synchronized void addUserInfo(WXUserInfo uinfo) {
		users.put(uinfo.openId, uinfo);
	}
	
	public synchronized boolean isOnlineUser(String uid, String token) {
		WXUserInfo uinfo = users.get(uid);
		if (uinfo != null && uinfo.accesToken.equals(token)) {
			return true;
		}
		
		return false;
	}
	
	private WXOnlineUserMgr(long areaId) {
		this.areaId = areaId;
	}
	
	private Map<String,WXUserInfo> users = new HashMap<String,WXUserInfo>();
	
	private final long areaId;
}
