package com.btiao.tzsc.service;

import java.util.ArrayList;
import java.util.List;

public class State {
	static public class Info {
		static public enum MsgType {
			text, pic
		}
		
		public MsgType t;
		public String content;
		public Info(MsgType t, String content) {
			this.t = t;
			this.content = content;
		}
	}
	
	static volatile long nextId = 100000000;
	static synchronized long genNextId() {
		//TODO persist it after modify.
		return nextId ++;
	}
	
	public final long id; //id of a state
	public String areaId; //小区名称
	public String userId; //用户名
	public List<Info> infos = new ArrayList<Info>(); //元信息描述
	
	public State(String name) {
		id = genNextId();
		this.userId = name;
	}
}
