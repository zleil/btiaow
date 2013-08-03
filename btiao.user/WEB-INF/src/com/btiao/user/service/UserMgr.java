package com.btiao.user.service;

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
	
	
}
