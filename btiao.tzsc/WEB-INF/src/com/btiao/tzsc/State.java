package com.btiao.tzsc;

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
	
	public final long id;
	public String areaId;
	public String name;
	public List<Info> infos = new ArrayList<Info>();
	
	public State(String name) {
		id = genNextId();
		this.name = name;
	}
}
