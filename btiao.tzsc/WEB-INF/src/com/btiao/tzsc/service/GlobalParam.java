package com.btiao.tzsc.service;

public class GlobalParam {
	//For WXPutStateMgr
	static public volatile long wxputstate_pageviewimeout = 1000*60*1; //2 hours
	static public volatile long wxputstate_statetimeout = 1000*60*2; //2 hours
	static public volatile long wxputstate_circle = 1000*60; //1 hour
	static public volatile int max_switch_wuping = 8;
}
