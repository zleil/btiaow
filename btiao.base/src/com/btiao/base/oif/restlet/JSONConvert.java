package com.btiao.base.oif.restlet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.utils.BTiaoLog;

public class JSONConvert {
	static public void main(String[] args) throws Exception {
		class A {
			public String attr1 = "a";
			public int attr2 = 2;
			public String toString() {
				return "attr1="+attr1+",attr2="+attr2;
			}
		}
		
		JSONConvert cvt = new JSONConvert();
		A oo = new A(); oo.attr1 = "json attr1 xxx"; oo.attr2 = 1234;
		A o = new A();
		JSONObject jo = new JSONObject();
		jo.put("attr1", oo.attr1);
		jo.put("attr2", oo.attr2);
		cvt.json2obj(jo, o, new ArrayList<String>());
		System.out.println(o);
		assert(oo.toString().equals(o.toString()));
		
		JSONObject joo = new JSONObject();
		cvt.obj2json(oo, joo);
		System.out.println(joo);
		assert(joo.get("attr1").equals(oo.attr1));
		assert(joo.get("attr2").equals(oo.attr2));
	}
	
	public void json2obj(JSONObject jo, Object obj, Collection<String> attrList) {
		Field[] fields = obj.getClass().getFields();
		for (Field f : fields) {
			String name = f.getName();
			String typeName = f.getType().getName();
			try {
				Object valueObj = jo.get(name);
				if (valueObj == null) {
					continue;
				}
				
				String valueStr = valueObj.toString();
				Object value = valueStr2Obj(valueStr, typeName);
				f.set(obj, value);
				
				attrList.add(name);
			} catch (Exception e) {
//				String warnMsg = e + 
//						"\n obj.className=" + obj.getClass().getName() +
//						"\n obj.toString=" + obj +
//						"\n json=" + jo;
//				log.warn(errMsg);
			}
		}
	}
	
	public void array2json(Collection<Object> objs, JSONArray array) {
		for (Object obj : objs) {
			JSONObject jo = new JSONObject();
			array.put(jo);
			obj2json(obj, jo);
		}
	}
	
	public void obj2json(Object obj, JSONObject jo) {
		Field[] fields = obj.getClass().getFields();
		for (Field f : fields) {
			String name = f.getName();
			try {
				Object value = f.get(obj);
				jo.put(name, value);
			} catch (Exception e) {
				String errMsg = e + 
						"\n obj.className=" + obj.getClass().getName() +
						"\n obj.toString=" + obj +
						"\n json=" + jo;
				log.error(errMsg);
				return;
			}
		}
	}
	
	private Object valueStr2Obj(String valueStr, String typeName) 
	throws BTiaoExp {
		if (typeName.equals("".getClass().getName())) {
			return valueStr;
		} else if (typeName.equals("long")) {
			return Long.parseLong(valueStr);
		} else if (typeName.equals("int")) {
			return Integer.parseInt(valueStr);
		} else if (typeName.equals("float")) {
			return Float.parseFloat(valueStr);
		} else if (typeName.equals("double")) {
			return Double.parseDouble(valueStr);
		} else {
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}
	}
	
	private Logger log = BTiaoLog.get();
}
