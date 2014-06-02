package com.btiao.tzsc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WXPutStateMgr {
	static enum MsgType {
		text, pic
	}
	
	static class Info {
		public MsgType t;
		public String content;
		public Info(MsgType t, String content) {
			this.t = t;
			this.content = content;
		}
	}
	
	static class State {
		public String name;
		public List<Info> content = new ArrayList<Info>();
		
		public State(String name) {
			this.name = name;
		}
	}
	
	static private WXPutStateMgr inst;
	
	static synchronized WXPutStateMgr instance() {
		if (inst == null) {
			inst = new WXPutStateMgr();
		}
		
		return inst;
	}
	
	public void startPut(String name) {
		if (!curPuts.containsKey(name)) {
			curPuts.put(name, new State(name));
		}
	}
	
	public String putTextMsg(String name, String text) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			state = new State(name);
			curPuts.put(name, state);
		}
		
		Info info = new Info(MsgType.text, text);
		state.content.add(info);
		
		return "发送文字、图片，继续描述待交换物品\n\n" + "发送数字 0 ，取消描述\n" +
		"发送数字 1 ，提交待描述物品";
	}
	
	public String putUrlMsg(String name, String url) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			state = new State(name);
			curPuts.put(name, state);
		}
		
		Info info = new Info(MsgType.pic, url);
		state.content.add(info);
		
		return "发送文字、图片，继续描述待交换物品\n\n" + "发送数字 0 ，取消描述\n" +
		"发送数字 1 ，提交待描述物品";
	}
	
	public String endPut(String name) {
		if (!curPuts.containsKey(name)) {
			return "";
		}
		
		//TODO add to db
		State state = this.curPuts.remove(name);
		List<State> states = all.get(name);
		if (states == null) {
			states = new ArrayList<State>();
			all.put(name, states);
		}
		states.add(state);
		
		return "又多了件待交换物品，耐心等候吧";
	}
	
	public String search(String name, String text) {
		if (this.curPuts.containsKey(name)) {
			return "您正在提交待交换物品，请先取消或提交，再进行搜索\n\n"+
					"发送数字 0 先取消待提交物品\n" + 
					"发送数字 1 ，提交待描述物品";
		}
		
		return "search return text, list of all to switch";
	}
	
	public void more(String name) {
		
	}
	
	private Map<String,State> curPuts = new HashMap<String,State>();
	
	private Map<String,List<State>> all = new HashMap<String,List<State>>();
}
