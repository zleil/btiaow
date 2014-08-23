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
	
	public synchronized List<WPState> searchState(String text) {
		//TODO implements search action
		
		List<WPState> ret = new ArrayList<WPState>();
		for (List<WPState> sts : all.values()) {
			ret.addAll(sts);
		}
		
		return ret;
	}
	
	public synchronized WPState getState(long stateId) {
		WPState s = stateId2State.get(stateId);
		return s;
	}
	
	public synchronized List<WPState> getAllStateByUserName(String name) {
		List<WPState> ret = new ArrayList<WPState>();
		
		List<WPState> self = all.get(name);
		if (self != null) {
			ret.addAll(self);
		}
		
		return ret;
	}
	
	public synchronized String getall() {
		return this.all.toString();
	}
	
	public synchronized int addState(String userName, WPState state) {
		List<WPState> states = all.get(userName);
		if (states == null) {
			states = new ArrayList<WPState>();
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
		List<WPState> sts = all.get(userName);
		if (idx >= 1 && idx <= sts.size()) {
			WPState s = sts.remove(idx-1);
			stateId2State.remove(s.id);
			
			if (sts.size() == 0) {
				all.remove(userName);
			}
			
			isChanged = true;
			return sts.size();
		} else {
			return -1;
		}
	}
	
	public synchronized int delOneStateById(long stateid) {
		WPState state = stateId2State.get(stateid);
		if (state == null) {
			return ErrCode.no_such_state;
		}
		
		List<WPState> states = all.get(state.userId);
		for (int idx=0; idx<states.size(); ++idx) {
			if (state.id == states.get(idx).id) {
				states.remove(idx);
				stateId2State.remove(stateid);
				
				if (states.size() == 0) {
					all.remove(state.userId);
				}
				
				isChanged = true;
				return ErrCode.success;
			}
		}

		return ErrCode.internel_error;
	}
	
	public boolean isChanged() {
		return isChanged;
	}
	
	public synchronized void setUnchanged() {
		isChanged = false;
	}
	
	static public void main(String[] args) {
		StateMgr.instance(0).addState("abc", new WPState(""));
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
				
				MyLogger.get().info("persist statemgr once: areaId="+areaId);
				
				new PersistObj().moveAndBack(StateMgr.persistFn+"_"+areaId);
				
				synchronized(StateMgr.instance(areaId)) {
					new PersistObj().persist(StateMgr.persistFn+"_"+areaId, all);
					
					StateMgr.instance(areaId).setUnchanged();
				}
			}
		});
		
		//增加pc测试用户zleil的物品信息
		WPState state = new WPState("zleil");
		state.areaId = areaId;
		WPState.Info info = new WPState.Info(WPState.Info.MsgType.text, "test title");
		state.getInfos().add(info);
		WPState.Info info2 = new WPState.Info(WPState.Info.MsgType.phone, "13812345678");
		state.getInfos().add(info2);
		WPState.Info info3 = new WPState.Info(WPState.Info.MsgType.pic, "http://mmbiz.qpic.cn/mmbiz/oTkDYnjzyLSDU36dMoFn82Q5WunXZibINiboY41T6JiaicRmKo3odoWF0gzAXVOgyyoibIIzmg3PbF5sah4q1Euibyyg/0");
		state.getInfos().add(info3);
		
		addState("zleil", state);
		
		WPState state2 = new WPState("zleil");
		state2.areaId = areaId;
		WPState.Info info4 = new WPState.Info(WPState.Info.MsgType.text, "test title2");
		state.getInfos().add(info4);
		state2.getInfos().add(info2);
		state2.getInfos().add(info);
		state2.getInfos().add(info3);
		
		addState("zleil", state2);
	}
	
	@SuppressWarnings("unchecked")
	private void load() throws Exception {
		Object persistAll = new PersistObj().load(StateMgr.persistFn+"_"+areaId);
		if (persistAll == null) {
			return;
		}
		
		all = (Map<String, List<WPState>>) persistAll;
		
		Set<Entry<String,List<WPState>>> entries = all.entrySet();
		for (Entry<String,List<WPState>> entry : entries) {
			for (WPState state : entry.getValue()) {
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
	private Map<String,List<WPState>> all = new HashMap<String,List<WPState>>();
	private Map<Long,WPState> stateId2State = new HashMap<Long,WPState>();
}
