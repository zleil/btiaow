package com.btiao.base.oif.restlet;

import java.lang.reflect.Method;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;

public abstract class ResBTBase extends ServerResource {
	static private enum OP {
		post,put,del,get
	};
	
	static public void main(String[] args) throws Exception {
		JSONObject jo = new JSONObject();
		jo.put("a", 1);
		jo.put("a", 2);
		System.out.println(jo.get("a"));
	}
	
	public Logger logger = LogManager.getRootLogger();
	
	@Get(value="json")
	public final JsonRepresentation btiaoGet() {
		return commonPPD(null, OP.get);
	}
	
	@Put(value="json:json")
	public JsonRepresentation btiaoPut(JsonRepresentation arg) {
		return commonPPD(arg, OP.put);
	}
	
	@Delete(value="json:json")
	public JsonRepresentation btiaoDel(JsonRepresentation arg) {
		return commonPPD(arg, OP.del);
	}
	
	@Post(value="json:json")
	public JsonRepresentation btiaoPost(JsonRepresentation arg) {
		return commonPPD(arg, OP.post);
	}
	
	/**
	 * get process.
	 * @param form URI argument, not null
	 * @return the return object will be converted to the 'content'<br>
	 *         attribute of the return JSON object.
	 * @throws BTiaoExp if process failed, must throw the excepion with <br>
	 *         an error code assigned in the exception object.
	 */
	protected abstract Object get(Form form) throws BTiaoExp;
	
	protected abstract Object put(Object arg) throws BTiaoExp;
	
	protected abstract Object post(Object arg) throws BTiaoExp;
	
	protected abstract Object del(Object arg) throws BTiaoExp;
	
	private Object cvtJson2Obj(OP op, JSONObject jo) throws Exception {
		Method m = this.getClass().getMethod("del");
		JsonCvtInfo annot = m.getAnnotation(JsonCvtInfo.class);
		String objClassName = annot.objClassName();
		Object obj = Class.forName(objClassName).newInstance();
		new JSONConvert().json2obj(jo, obj);
		return obj;
	}
	
	private JSONObject setContentOfJRO(JSONObject jro, Object contentRet) {
		if (contentRet == null) {
			return jro;
		}
		
		new JSONConvert().obj2json(contentRet, jro);
		return jro;
	}
	
	private JsonRepresentation commonPPD(JsonRepresentation arg, OP op) {
		JSONObject jro = new JSONObject(); //return object
		setRstOfJRO(jro, ErrCode.SUCCESS);
		
		try {
			Object contentRet = null;
			
			try {
				if (op == OP.get) {
					Form form = this.getReference().getQueryAsForm();
					contentRet = get(form);
				} else {
					JSONObject jao = arg.getJsonObject();
					
					Object argObj = cvtJson2Obj(op, jao);
					if (op == OP.del) {						
						contentRet = del(argObj);
					} else if (op == OP.post) {
						contentRet = post(argObj);
					} else if (op == OP.put) {
						contentRet = put(argObj);
					}
				}
			} catch (BTiaoExp e) {
				setRstOfJRO(jro, e.errNo);
			} catch (Throwable e) {
				e.printStackTrace();
				setRstOfJRO(jro, ErrCode.UNKOWN_ERR);
			}
			
			setContentOfJRO(jro, contentRet);
		} catch (Exception e) {
			e.printStackTrace();
			setRstOfJRO(jro, ErrCode.WRONG_PARAM);
		}
		
		return new JsonRepresentation(jro);
	}
	
	private JSONObject setRstOfJRO(JSONObject jro, int code) {
		try {
			jro.put("errCode", code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jro;
	}
}
