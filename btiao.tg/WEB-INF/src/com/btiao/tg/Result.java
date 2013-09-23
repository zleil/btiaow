package com.btiao.tg;

import java.util.HashMap;
import java.util.Map;

public class Result {
	static public int SUCCESS = 0;
	static public int ARG_ERROR = 1;
	static public int TG_ERROR = 2;
	static public int TG_GEN_FDB_FAILED = 3;
	static public int TG_GEN_FDB_DYNAMIC_FAILED = 4;
	static public int TG_GEN_POSDB_OPEN_FAILED = 5;
	
	static public Map<Integer,String> cn = new HashMap<Integer,String>();
	
	static public String desc(int rst) {
		return cn.get(rst);
	}
	
	static {
		cn.put(SUCCESS, "");
		cn.put(ARG_ERROR, "http参数错误");
		cn.put(TG_ERROR, "团购模块故障");
	}
}
