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
		
		return "�������� 1 �������ύ��������Ʒ\n�������� 0 ��ȡ�����α����ύ";
	}
	
	public String putUrlMsg(String name, String url) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			state = new State(name);
			curPuts.put(name, state);
		}
		
		Info info = new Info(MsgType.pic, url);
		state.content.add(info);
		
		return "�������� 1 �������ύ��������Ʒ\n�������� 0 ��ȡ�����α����ύ"; 
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
		
		return "�ύ�ɹ����������ĵ�x����������Ʒ";
	}
	
	public String search(String name, String text) {
		if (this.curPuts.containsKey(name)) {
			return "������δ�ύ�Ĵ�������Ʒ��Ϣ\n\n�������� 0 ��ȡ�����α����ύ��������";
		}
		
		return "search return text, list of all to switch";
	}
	
	public void more(String name) {
		
	}
	
	private Map<String,State> curPuts = new HashMap<String,State>();
	
	private Map<String,List<State>> all = new HashMap<String,List<State>>();
}
