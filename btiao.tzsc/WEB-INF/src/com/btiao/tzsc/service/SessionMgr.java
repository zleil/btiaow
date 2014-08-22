package com.btiao.tzsc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.btiao.tzsc.weixin.Tip;
import com.btiao.tzsc.weixin.WXApi;
import com.btiao.tzsc.weixin.WXMsg;
import com.btiao.tzsc.weixin.WXPutStateMgr;

public class SessionMgr {
	static public enum SessionState {
		normal
	}
	
	static class Session {
		public SessionState s;
		public long lastAccessTime;
		
		public Session() {
			s = SessionState.normal;
			lastAccessTime = System.currentTimeMillis();
		}
	}
	
	static private Map<Long,SessionMgr> insts = new HashMap<Long,SessionMgr>();
	
	static public synchronized SessionMgr instance(long areaId) {
		SessionMgr inst = insts.get(areaId);
		if (inst == null) {
			inst = new SessionMgr(areaId);
			inst.addSession("zleil", "zleil");
			insts.put(areaId, inst);
			return inst;
		}
		
		return inst;
	}
	
	public synchronized void addSession(String uid, String newToken) {
		Map<String,Session> ss = users.get(uid);
		if (ss == null) {
			ss = new HashMap<String,Session>();
			users.put(uid, ss);
			ss.put(newToken, new Session());
			return;
		}
		
		if (ss.size() < GlobalParam.max_session_per_user) {
			ss.put(newToken, new Session());
			return;
		}
		
		//找到最老的那个session，删除它，然后新增这个
		Session oldest = null;
		String token = null;
		for (Entry<String,Session> entry : ss.entrySet()) {
			Session s = entry.getValue();
			if (oldest == null) {
				oldest = s;
				token = entry.getKey();
				continue;
			}
			
			if (oldest.lastAccessTime > s.lastAccessTime) {
				oldest = s;
				token = entry.getKey();
			}
		}
		
		ss.remove(token);
		
		//增加新的会话
		ss.put(newToken, new Session());
	}
	
	public synchronized void removeSession(String uid, String token) {
		Map<String,Session> ss = users.get(uid);
		if (ss == null) {
			return;
		}
		
		ss.remove(token);
		if (ss.size() == 0) {
			users.remove(uid);
		}
	}
	
	public synchronized boolean isOnlineUser(String uid, String token) {
		Map<String,Session> ss = users.get(uid);
		if (ss == null) {
			return false;
		}
		
		Session state = ss.get(token);
		if (state != null && state.s == SessionState.normal) {
			return true;
		}
		
		return false;
	}
	
	public synchronized String getall() {
		return this.users.toString();
	}
	
	private SessionMgr(final long areaId) {
		this.areaId = areaId;
		
		Thread sessionTimeoutThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(GlobalParam.session_circle);
						
						SessionMgr inst = SessionMgr.instance(areaId);
						synchronized (inst) {
							for (Entry<String,Map<String,Session>> entry : inst.users.entrySet()) {
								Map<String,Session> sessions = entry.getValue();
								String usrId = entry.getKey();
								
								Set<String> tokens = sessions.keySet();
								for (String token : tokens) {
									Session s = sessions.get(token);
									
									long cur = System.currentTimeMillis();
									if (timeout(s)) {
										sessions.remove(token);
									}
								}
							}
						}
					} catch (Throwable e) {
						MyLogger.get().error("get a error in timeoutDelCurState thread!", e);
					}
				}
			}
			
			boolean timeout(Session s) {
				return (System.currentTimeMillis()-s.lastAccessTime) >= GlobalParam.session_timeout;
			}
		};
		
		sessionTimeoutThread.setName("sessionTimeoutThread");
		sessionTimeoutThread.start();
	}
	
	//usrId-><token->Session>
	private Map<String,Map<String,Session>> users = new HashMap<String,Map<String,Session>>();
	
	private final long areaId;
}
