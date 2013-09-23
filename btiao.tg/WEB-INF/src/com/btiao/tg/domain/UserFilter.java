package com.btiao.tg.domain;

import java.util.ArrayList;
import java.util.List;

public class UserFilter {
	static public class Item {
		public String n;
		public String v;
		public String op;
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof Item)) return false;
			
			Item oo = (Item) o;
			return n.equals(oo.n) && v.equals(oo.v);
		}
		public String toString() {
			return n + op + v;
		}
	}
	static public void main(String[] args) {
		UserFilter fs = new UserFilter();
	
		String itemStr1 = "px=1";
		String itemStr2 = "py=2";
		fs.addFilter(itemStr1+";"+itemStr2);
		assert(fs.fs.size() == 1);
		
		Item item1 = fs.genItem(itemStr1);
		Item item2 = fs.genItem(itemStr2);
		assert(fs.fs.get(0).get(0).equals(item1));
		assert(fs.fs.get(0).get(1).equals(item2));
		
		//System.out.println(fs);
		
		try {
			assert(false);
			System.out.println("pls use -ea VM argument!");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}
	
	/**
	 * 通过字符串构造查询结构：
	 * STR := EXP [;STR]
	 * EXP := id OP value
	 * OP := "=" || "<" || ">" || ">=" || "<="
	 */
	public boolean addFilter(String s) {
		boolean r = false;
		
		String[] exps = s.split(";");
		if (exps == null || exps.length == 0) return true;
		
		List<Item> f = new ArrayList<Item>();
		for (String exp : exps) {
			Item item = genItem(exp);
			if (item == null) {
				System.out.println("err filter: exp=" + exp + "\n\t at: " + s);
				continue;
			}
			f.add(item);
		}
		
		if (f.size() == 0) return true;
		
		fs.add(f);
		return r;
	}
	
	private Item genItem(String iStr) {
		String[] splitIStr = iStr.split(">=");
		if (splitIStr != null && splitIStr.length == 2) {
			return null; //TODO 
		}
		splitIStr = iStr.split("<=");
		if (splitIStr != null && splitIStr.length == 2) {
			return null; //TODO 
		}
		splitIStr = iStr.split("<>");
		if (splitIStr != null && splitIStr.length == 2) {
			return null; //TODO 
		}
		splitIStr = iStr.split(">");
		if (splitIStr != null && splitIStr.length == 2) {
			return null; //TODO 
		}
		splitIStr = iStr.split("<");
		if (splitIStr != null && splitIStr.length == 2) {
			return null; //TODO 
		}
		splitIStr = iStr.split("=");
		if (splitIStr != null && splitIStr.length == 2) {
			Item item = new Item();
			item.n = splitIStr[0].trim();
			item.v = splitIStr[1].trim();
			if (item.n.equals("") || item.v.equals("")) {
				return null;
			}
			
			item.op = "=";
			return item;
		}
		
		return null;
	}
	
	@Override
	public int hashCode() {
		return fs.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		
		if (!(o instanceof UserFilter)) {
			return false;
		}
		
		UserFilter oo = (UserFilter)o;

		return fs.equals(oo.fs);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("city="+city);
		sb.append(",lon="+uLon);
		sb.append(",lat="+uLat);
		
//		sb.append("{");
//		for (List<Item> f : fs) {
//			sb.append("{");
//			for (Item it : f) {
//				sb.append(it.toString());
//				sb.append(",");
//			}
//			sb.append("},");
//		}
//		sb.append("}");
		
		sb.append("}");
		return sb.toString();
	}
	
	public List<List<Item>> fs = new ArrayList<List<Item>>();
	
	public String city;
	public int uLon = -1;
	public int uLat = -1;
}
