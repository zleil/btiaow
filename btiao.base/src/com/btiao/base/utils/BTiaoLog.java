package com.btiao.base.utils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

public class BTiaoLog {
	static public Logger get() {
		return Logger.getLogger("btiao");
	}
	
	static public Logger get(String name) {
		return Logger.getLogger(name);
	}
	
	static public void logExp(Logger log, Throwable e) {
		logExp(log, e, "");
	}
	
	static public void logExp(Logger log, Throwable e, String extInfo) {
		OutputStream o = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(o);
		p.append("there's a exception,extInfo=[");p.append(extInfo);p.append("]\n");
		e.printStackTrace(p);
		
		System.err.println(o);
		log.error(o);
	}
}
