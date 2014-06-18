package com.btiao.tzsc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class StateMgr {
	static private StateMgr inst = null;
	static private String persistDir = "tzscdb";
	static private String persistFn = "tzsc.db";
	
	static public synchronized StateMgr instance() {
		if (inst == null) {
			inst = new StateMgr();
		}
		
		return inst;
	}
	
	public synchronized List<State> searchState(String text) {
		//TODO implements search action
		
		List<State> ret = new ArrayList<State>();
		for (List<State> sts : all.values()) {
			ret.addAll(sts);
		}
		
		return ret;
	}
	
	public synchronized State getState(long stateId) {
		State s = stateId2State.get(stateId);
		return s;
	}
	
	public synchronized List<State> getAllStateByUserName(String name) {
		List<State> ret = new ArrayList<State>();
		
		List<State> self = all.get(name);
		if (self != null) {
			ret.addAll(self);
		}
		
		return ret;
	}
	
	public synchronized int addState(String userName, State state) {
		List<State> states = all.get(userName);
		if (states == null) {
			states = new ArrayList<State>();
			all.put(userName, states);
			
			stateId2State.put(state.id, state);
		}
		states.add(state);
		
		isChanged = true;
		
		return states.size();
	}
	
	/**
	 * 删除一个物品
	 * @param userName
	 * @param idx
	 * @return -1, 说明要删除的物品不存在
	 * 		否则，说明删除后的物品总数
	 */
	public synchronized int delOneState(String userName, int idx) {
		List<State> sts = all.get(userName);
		if (idx >= 1 && idx <= sts.size()) {
			State s = sts.remove(idx-1);
			stateId2State.remove(s.id);
			
			isChanged = true;
			return sts.size();
		} else {
			return -1;
		}
	}
	
	public boolean isChanged() {
		return isChanged;
	}
	
	public synchronized void setUnchanged() {
		isChanged = false;
	}
	
	static public void main(String[] args) {
		StateMgr.instance().addState("abc", new State(""));
	}
	
	private StateMgr() {
		try {
			load();
		} catch (Exception e) {
			MyLogger.get().error("load persist failed!", e);
		}
		
		Thread persistTh = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000*30);
						
						if (!StateMgr.instance().isChanged()) {
							continue;
						}
						
						MyLogger.get().info("persist once");
						
						String dest = persistDir+File.separator+StateMgr.persistFn + "_" + System.currentTimeMillis();
						new File(StateMgr.persistFn).renameTo(new File(dest));
						
						synchronized(StateMgr.instance()) {
							persist();
							
							StateMgr.instance().setUnchanged();
						}
					} catch (Throwable e) {
						MyLogger.get().error("persist failed!", e);
					}
				}
			}
		};
		
		persistTh.setName("tzsc_persist");
		persistTh.start();
	}
	
	private void load() throws Exception {
		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		try {
			File file = new File(persistDir+File.separator+persistFn);
			if (file.exists()) {
				fin = new FileInputStream(file);
				objIn = new ObjectInputStream(fin);
				all = (Map<String, List<State>>) objIn.readObject();
				
				Set<Entry<String,List<State>>> entries = all.entrySet();
				for (Entry<String,List<State>> entry : entries) {
					for (State state : entry.getValue()) {
						this.stateId2State.put(state.id, state);
					}
				}
			}			
		} finally {
			if (objIn != null) objIn.close();
			if (fin != null) fin.close();
		}
		
		
	}
	
	private void persist() throws Exception {
		//1. write to a tmp file
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		try {
			fout = new FileOutputStream(new File(persistDir+File.separator+persistFn+".tmp"));
			objOut = new ObjectOutputStream(fout);
			objOut.writeObject(all);
		} finally {
			if (objOut != null) objOut.close();
			if (fout != null) fout.close();
		}
		
		//2. rename to the latest db file
		File file = new File(persistDir+File.separator+persistFn+".tmp");
		if (file.exists()) {
			file.renameTo(new File(persistDir+File.separator+persistFn));
		}
	}
	
	
	private volatile boolean isChanged = false;
	private Map<String,List<State>> all = new HashMap<String,List<State>>();
	private Map<Long,State> stateId2State = new HashMap<Long,State>();
}
