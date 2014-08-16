package com.btiao.tzsc.service;

import java.util.HashMap;
import java.util.Map;

public class ComDataMgr<DataType> {
	static public synchronized <DataType> ComDataMgr<DataType> instance(String dbId, long areaId) {
		String instId = dbId+"."+areaId;
		
		@SuppressWarnings("unchecked")
		ComDataMgr<DataType> inst = (ComDataMgr<DataType>) insts.get(instId);
		
		if (inst == null) {
			inst = new ComDataMgr<DataType>(dbId, areaId);
			insts.put(instId, inst);
		}
		
		return inst;
	}
	
	static public void main(String[] args) {
		ComDataMgr<UserInfo> a = ComDataMgr.<UserInfo>instance("UserInfo", 123);
		System.out.println(a.all);
	}
	
	static private Map<String,ComDataMgr<?>> insts = new HashMap<String,ComDataMgr<?>>();

	public synchronized void add(String key, DataType one) {
		all.put(key, one);
		this.changed = true;
	}
	
	public synchronized DataType get(String id) {
		return this.all.get(id);
	}
	
	private ComDataMgr(final String dbId, final long areaId) {
		this.areaId = areaId;
		this.dbId = dbId;
		
		persistFn = "tzsc."+dbId+".db."+areaId;
		
		load();

		if (dbId.equals("UserInfo")) {
			// add a internel test account
			UserInfo zleil = new UserInfo();
			zleil.usrId = "zleil";
			zleil.nick = "zhanglei";
			add(zleil.usrId, ((DataType)zleil));
		}
		
		PersistObj.addBackTask(new Runnable() {
			
			@Override
			public void run() {
				ComDataMgr<?> inst = ComDataMgr.instance(dbId, areaId);
				synchronized (inst) {
					if (!inst.hasChanged()) {
						return;
					}
					
					new PersistObj().persist(persistFn, all);
					
					inst.setUnchanged();
				}
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
		Object persistAll = new PersistObj().load(persistFn);
		if (persistAll == null) {
			MyLogger.get().warn("get persist areamgr failed!");
			return;
		}
		
		all = (HashMap<String,DataType>) persistAll;
	}
	
	private void setUnchanged() {
		changed = false;
	}
	
	private boolean hasChanged() {
		return changed;
	}
	
	private HashMap<String,DataType> all = new HashMap<String,DataType>();
	
	private volatile boolean changed = false;
	
	private long areaId;
	
	private String dbId;
	
	private String persistFn;
}
