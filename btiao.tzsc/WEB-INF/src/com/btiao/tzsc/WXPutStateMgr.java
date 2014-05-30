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
		
		return "发送数字 1 ，结束提交待交换物品\n发送数字 0 ，取消本次本次提交";
	}
	
	public String putUrlMsg(String name, String url) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			state = new State(name);
			curPuts.put(name, state);
		}
		
		Info info = new Info(MsgType.pic, url);
		state.content.add(info);
		
		return "发送数字 1 ，结束提交待交换物品\n发送数字 0 ，取消本次本次提交"; 
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
		
		return "提交成功，这是您的第x个待交换物品";
	}
	
	public String search(String name, String text) {
		if (this.curPuts.containsKey(name)) {
			return "您还有未提交的待交换物品信息\n\n发送数字 0 ，取消本次本次提交，再搜索";
		}
		
		return "search return text, list of all to switch";
	}
	
	public void more(String name) {
		
	}
	
	private Map<String,State> curPuts = new HashMap<String,State>();
	
	private Map<String,List<State>> all = new HashMap<String,List<State>>();
}
