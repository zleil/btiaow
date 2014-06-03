package com.btiao.tzsc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StateMgr {
	static private StateMgr inst = null;
	
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
	
	public synchronized List<State> getAllStateByUserName(String name) {
		List<State> ret = new ArrayList<State>();
		
		List<State> self = all.get(name);
		if (self != null) {
			ret.addAll(self);
		}
		
		return ret;
	}
	
	public synchronized int addState(String userName, State state) {
		//TODO add to db
		
		List<State> states = all.get(userName);
		if (states == null) {
			states = new ArrayList<State>();
			all.put(userName, states);
		}
		states.add(state);
		
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
			sts.remove(idx-1);
			return sts.size();
		} else {
			return -1;
		}		
	}
	
	private Map<String,List<State>> all = new HashMap<String,List<State>>();
}
