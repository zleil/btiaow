package com.btiao.tzsc.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class State implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5974420041156366603L;
	
	private static String nextIdFile = "stateNextId.db";

	static public class Info implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 2937649078497298008L;
		static public class MsgType {
			static public int text = 1;
			static public int pic = 2;
		}
		
		public int t;
		public String content;
		public Info(int t, String content) {
			this.t = t;
			this.content = content;
		}
	}
	
	static volatile Long nextId = new Long(100000000);
	
	static {
		Long persistedNextId = (Long) new PersistObj().load(nextIdFile);
		if (persistedNextId != null) {
			nextId = persistedNextId;
			
			MyLogger.get().info("get persist stateNextId: " + persistedNextId);
		}
		
		PersistObj.addBackTask(nextIdFile);
	}
	
	static public void main(String[] args){
		nextId ++;
		nextId ++;
		System.out.println(nextId);
	}
	
	static synchronized long genNextId() {
		long ret = nextId++;
		new PersistObj().persist(nextIdFile, nextId);
		
		return ret;
	}
	
	public final long id; //id of a state
	public long areaId; //小区名称
	public String userId; //用户名
	public long publishTime; //发布时间
	public List<Info> infos = new ArrayList<Info>(); //元信息描述
	
	public State(String name) {
		id = genNextId();
		this.userId = name;
	}
}
