package com.btiao.tzsc.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4186356415166257044L;

	static public class Reply {
		public Reply(String usrId, String statement) {
			time = System.currentTimeMillis();
			this.usrId = usrId;
			this.statement = statement;
		}
		
		public String usrId;
		public String statement;
		
		public long time;
	}
	
	static synchronized long genNextId() {
		long ret = nextId++;
		new PersistObj().persist(nextIdFile, nextId);
		
		return ret;
	}
	
	static String nextIdFile = "questionNextId.db";
	static volatile Long nextId = new Long(100000000);
	
	static {
		Long persistedNextId = (Long) new PersistObj().load(nextIdFile);
		if (persistedNextId != null) {
			nextId = persistedNextId;
			
			MyLogger.get().info("get persist questionNextId: " + persistedNextId);
		}
	}
	
	public Question(long stateId, String usrId, String statement) {
		id = genNextId();
		time = System.currentTimeMillis();
		
		this.stateId = stateId;
		this.usrId = usrId;
		this.statement = statement;
	}
	
	public final long id;

	public final long time;
	
	public long stateId;
	public String usrId;
	public String statement;
	
	public List<Reply> replies = new ArrayList<Reply>();
}
