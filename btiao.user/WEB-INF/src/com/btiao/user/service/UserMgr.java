package com.btiao.user.service;

import java.util.HashMap;
import java.util.Map;
import com.btiao.user.domain.BTiaoUser;

public class UserMgr {
	static public void main(String[] args) {
		try {
			assert(false);
			System.out.println("please use VM argument '-ea'.");
		} catch (Exception e) {
			System.out.println("success!");
		}
	}
	static public synchronized UserMgr instance() {
		if (inst == null) {
			inst = new UserMgr();
		}
		return inst;
	}
	
	static private UserMgr inst;
	
	public void addUser(BTiaoUser u) {
		uId2Usr.put(u.id, u);
	}
	
	public void delUser(String uId) {
		uId2Usr.remove(uId);
	}
	
	public void updateUser(BTiaoUser u) {
		uId2Usr.put(u.id, u);
	}
	
	public BTiaoUser getUser(String uId) {
		return uId2Usr.get(uId);
	}
	
	private Map<String,BTiaoUser> uId2Usr = new HashMap<String,BTiaoUser>();
}
