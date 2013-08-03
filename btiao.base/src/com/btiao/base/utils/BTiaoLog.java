package com.btiao.base.utils;

import org.apache.log4j.Logger;

public class BTiaoLog {
	static public Logger get() {
		return Logger.getLogger("btiao");
	}
	
	static public Logger get(String name) {
		return Logger.getLogger(name);
	}
}
