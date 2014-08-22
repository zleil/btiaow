package com.btiao.tzsc.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
	static public void main(String[] args) {
		MyLogger.get().warn("here");
	}
	
	static public Logger get() {
		return LogManager.getLogger("tzsc");
	}
	
	static public Logger getAttackLog() {
		return LogManager.getLogger("tzsc.attack");
	}
	
	static public Logger getSuggestion() {
		return LogManager.getLogger("suggestion");
	}
	
	static public Logger getAccess() {
		return LogManager.getLogger("tzsc.access");
	}
	
	static public Logger getUnknownAct() {
		return LogManager.getLogger("tzsc.unknownAct");
	}
	
	static public Logger getWXCur() {
		return LogManager.getLogger("wxcur");
	}
}
