package com.btiao.base.utils;

import java.util.ArrayList;

public class SimpleFunc {
	static public ArrayList<String> getArrayList(String ...strs) {
		ArrayList<String> r = new ArrayList<String>();
		for (String s : strs) {
			r.add(s);
		}
		
		return r;
	}
}
