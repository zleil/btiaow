package com.btiao.tzsc.service;

public class GlobalParam {
	/**
	 * 系统升级、打补丁时用。<br>
	 * 系统维护前，先设置次标志为true，然后通过过滤器确保服务器不处理任何外部请求。<br>
	 * 待内部存储的定时任务都结束后，再重启进程。<br>
	 */
	static public volatile boolean blockAllInput = false;
	
	//For WXPutStateMgr
	static public volatile long wxputstate_pageviewimeout = 1000*60*1; //2 hours
	static public volatile long wxputstate_statetimeout = 1000*60*2; //2 hours
	static public volatile long wxputstate_circle = 1000*60; //1 hour
	static public volatile int max_switch_wuping = 8;
	
	//session manager
	static public volatile long session_circle = 1000*60; //1 hour
	static public volatile long session_timeout = session_circle*2; //2 hours
	static public volatile int max_session_per_user = 5; //一个用户最多5处同时登陆
}
