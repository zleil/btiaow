package com.btiao.tzsc.service;

import java.util.HashMap;
import java.util.Map;

public class SessionMgr {
	static private Map<Long,SessionMgr> insts = new HashMap<Long,SessionMgr>();
	
	static public synchronized SessionMgr instance(long areaId) {
		SessionMgr inst = insts.get(areaId);
		if (inst == null) {
			inst = new SessionMgr(areaId);
			insts.put(areaId, inst);
			return inst;
		}
		
		return inst;
	}
	
	public synchronized void addUserInfo(String uid, String token) {
		users.put(uid, token);
	}
	
	public synchronized boolean isOnlineUser(String uid, String token) {
		String tokenCur = users.get(uid);
		if (tokenCur != null && tokenCur.equals(token)) {
			return true;
		}
		
		return false;
	}
	
	private SessionMgr(long areaId) {
		this.areaId = areaId;
	}
	
	//uid->token
	private Map<String,String> users = new HashMap<String,String>();
	
	private final long areaId;
}
