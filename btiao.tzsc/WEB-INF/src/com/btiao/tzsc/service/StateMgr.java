package com.btiao.tzsc.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class StateMgr {
	static private Map<Long,StateMgr> insts = new HashMap<Long,StateMgr>();
	static private String persistFn = "tzsc.db";
	
	static public synchronized StateMgr instance(long areaId) {
		StateMgr inst = insts.get(areaId);
		
		if (inst == null) {
			inst = new StateMgr(areaId);
			insts.put(areaId, inst);
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
		}
		states.add(state);
		stateId2State.put(state.id, state);
		
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
		StateMgr.instance(0).addState("abc", new State(""));
	}
	
	private StateMgr(final long areaId) {
		this.areaId = areaId;
		
		try {
			load();
		} catch (Exception e) {
			MyLogger.get().error("load persist failed!", e);
		}
		
		PersistObj.addBackTask(new Runnable() {
			@Override
			public void run() {
				if (!StateMgr.instance(areaId).isChanged()) {
					return;
				}
				
				MyLogger.get().info("persist once");
				
				new PersistObj().moveAndBack(StateMgr.persistFn);
				
				synchronized(StateMgr.instance(areaId)) {
					new PersistObj().persist(StateMgr.persistFn, all);
					
					StateMgr.instance(areaId).setUnchanged();
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void load() throws Exception {
		Object persistAll = new PersistObj().load(StateMgr.persistFn);
		if (persistAll == null) {
			return;
		}
		
		all = (Map<String, List<State>>) persistAll;
		
		Set<Entry<String,List<State>>> entries = all.entrySet();
		for (Entry<String,List<State>> entry : entries) {
			for (State state : entry.getValue()) {
				this.stateId2State.put(state.id, state);
			}
		}		
	}
	
	@Override
	public String toString() {
		return "StateMgr#" + areaId;
	}
	
	private final long areaId;
	
	private volatile boolean isChanged = false;
	private Map<String,List<State>> all = new HashMap<String,List<State>>();
	private Map<Long,State> stateId2State = new HashMap<Long,State>();
}
