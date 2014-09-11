package com.btiao.tzsc.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ComDataMgr<KeyType,DataType> {
	static public interface IScan<DataType> {
		boolean process(DataType d);
	}
	
	static public synchronized <KeyType,DataType> ComDataMgr<KeyType,DataType> instance(String dbId, Object ... subids) {
		StringBuilder sb = new StringBuilder();
		sb.append(dbId);
		if (subids != null) for (Object id : subids) {
			sb.append(".");
			sb.append(id);
		}
		
		String fulldbId = sb.toString();
		
		@SuppressWarnings("unchecked")
		ComDataMgr<KeyType,DataType> inst = (ComDataMgr<KeyType,DataType>) insts.get(fulldbId);
		
		if (inst == null) {
			inst = new ComDataMgr<KeyType,DataType>(fulldbId);
			insts.put(fulldbId, inst);
		}
		
		return inst;
	}
	
	static public void main(String[] args) {
		ComDataMgr<String,UserInfo> a = ComDataMgr.<String,UserInfo>instance("UserInfo", 123);
		System.out.println(a.all);
	}
	
	static private Map<String,ComDataMgr<?,?>> insts = new HashMap<String,ComDataMgr<?,?>>();

	public synchronized void add(KeyType key, DataType one) {
		all.put(key, one);
		this.changed = true;
	}
	
	public synchronized DataType remove(KeyType key) {
		return all.remove(key);
	}
	
	public synchronized DataType get(KeyType id) {
		return this.all.get(id);
	}
	
	public synchronized boolean exist(KeyType id) {
		return all.containsKey(id);
	}
	
	/**
	 * 返回扫描中匹配的数据对象
	 * @param scan
	 * @return 找不到返回null
	 */
	public synchronized DataType scan(IScan<DataType> scan) {
		for (Entry<KeyType,DataType> entry : all.entrySet()) {
			DataType v = entry.getValue();
			if (scan.process(v)) {
				return v;
			}
		}
		
		return null;
	}
	
	public synchronized String getall() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		boolean first = true;
		for (Entry<KeyType,DataType> entry : all.entrySet()) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append("\"");
			sb.append(entry.getKey()); //TODO 需要转义 "'" 字符
			sb.append("\":");
			sb.append(entry.getValue()); //TODO 需要转义 "'" 字符
		}
		
		sb.append("}");
		
		return sb.toString();
	}
	
	public synchronized int getTotal() {
		return all.size();
	}
	
	private ComDataMgr(final String fulldbId) {
		persistFn = "comdata."+fulldbId+".db";
		
		load();

		if (fulldbId.startsWith(UserInfo.class.getSimpleName())) {
			// add a internel test account
			UserInfo zleil = new UserInfo("zleil");
			zleil.nick = "zhanglei";
			add((KeyType)zleil.usrId, ((DataType)zleil));
		}
		
		PersistObj.addBackTask(new Runnable() {
			
			@Override
			public void run() {
				ComDataMgr<KeyType,DataType> inst = ComDataMgr.<KeyType,DataType>instance(fulldbId);
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
		
		all = (HashMap<KeyType,DataType>) persistAll;
	}
	
	private void setUnchanged() {
		changed = false;
	}
	
	private boolean hasChanged() {
		return changed;
	}
	
	private HashMap<KeyType,DataType> all = new HashMap<KeyType,DataType>();
	
	private volatile boolean changed = false;
	private final String persistFn;
}
