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
		return LogManager.getLogger("attack");
	}
	
	static public Logger getSuggestion() {
		return LogManager.getLogger("suggestion");
	}
	
	static public Logger getAccess() {
		return LogManager.getLogger("access");
	}
	
	static public Logger getUnknownAct() {
		return LogManager.getLogger("unknownAct");
	}
}
