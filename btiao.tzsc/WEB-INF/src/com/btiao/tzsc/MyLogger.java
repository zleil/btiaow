package com.btiao.tzsc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyLogger {
	static public void main(String[] args) {
		MyLogger.get().warn("here");
	}
	
	static public Logger get() {
		return LogManager.getLogger("tzsc");
	}
}
