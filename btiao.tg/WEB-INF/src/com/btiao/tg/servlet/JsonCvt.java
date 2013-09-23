package com.btiao.tg.servlet;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import org.json.JSONObject;
import com.btiao.base.oif.restlet.JSONConvert;
import com.btiao.tg.TgData;

public class JsonCvt {
	static public void main(String[] args) {
		TgData tg = new TgData();
		tg.desc = "hello!";
		tg.url = "http://ffda&fda";
		
		StringBuilder sb = new StringBuilder();
		System.out.println(obj(tg,sb, null));
	}
	static public StringBuilder obj(Object o, StringBuilder sb, Map<String,Boolean> maskAttr) {
		JSONObject jo = new JSONObject();
		new JSONConvert().obj2json(o, jo);
		return sb.append(jo.toString());
	}
	static public StringBuilder _obj(Object o, StringBuilder sb, Map<String,Boolean> maskAttr) {
		sb.append("{");
		Class<?> oClass = o.getClass();
		Field[] fields = oClass.getDeclaredFields();
		for (Field f : fields) {
			int mdfIdx = f.getModifiers();
			if (Modifier.isPublic(mdfIdx) && !Modifier.isStatic(mdfIdx)) {
				String name = f.getName();
				if (maskAttr != null && !maskAttr.containsKey(name)) {
					continue;
				}
				
				Object v = null;
				try {
					v = f.get(o);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				if (v == null) {
					v = "";
				}
				
				sb.append(name);
				sb.append(":");
				
				if (isStr(v)) {
					sb.append("\"");
					sb.append(cvtStr(v.toString()));
					sb.append("\"");
				} else {
					sb.append(v.toString());
				}
				sb.append(",");
			}
		}
		sb.append("}");
		return sb;
	}
	
	static private String cvtStr(String s) {
		return s.replace("\"", "\\\"");
	}
	
	static private boolean isStr(Object v) {
		try {
			Integer.parseInt(v.toString());
			return false;
		} catch (Exception e) {
			return true;
		}
	}
}
