package com.btiao.base.oif.restlet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.utils.BTiaoLog;

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
	
	static public JSONObject setRstOfJRO(JSONObject jro, int code) {
		try {
			jro.put("errCode", code);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jro;
	}
	
	static public String OBJID_WHEN_CREATE = "__n";
	
	@Get(value="json")
	public final JsonRepresentation btiaoGet() {
		pre();
		return commonPPD(null, OP.get);
	}
	
	@Put(value="json:json")
	public JsonRepresentation btiaoPut(JsonRepresentation arg) {
		pre(); 
		
		if (arg == null) {
			try {
				arg = new JsonRepresentation(this.getRequest().getEntity());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return commonPPD(arg, OP.put);
	}
	
	@Delete(value="json:json")
	public JsonRepresentation btiaoDel(JsonRepresentation arg) {
		pre();
		return commonPPD(arg, OP.del);
	}
	
	@Post(value="json:json")
	public JsonRepresentation btiaoPost(JsonRepresentation arg) {
		pre();
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
	
	/**
	 * put process.
	 * @param arg if you put @JsonCvtInfo on the implement method,<br>
	 *            such as @JsonCvtInfo(objClassName="com.btiao.user.BTiaoUser")<br>
	 *            then, you will get an object parameter of 'objClassName' type.<br>
	 *            otherwise, arg is null
	 * @return the return object will be converted to the 'content'<br>
	 *         attribute of the return JSON object.
	 * @throws BTiaoExp if process failed, must throw the excepion with <br>
	 *         an error code assigned in the exception object.
	 */
	protected abstract Object put(Object arg) throws BTiaoExp;
	
	/**
	 * post process.
	 * @param arg if you put @JsonCvtInfo on the implement method,<br>
	 *            such as @JsonCvtInfo(objClassName="com.btiao.user.BTiaoUser")<br>
	 *            then, you will get an object parameter of 'objClassName' type.<br>
	 *            otherwise, arg is null
	 * @return the return object will be converted to the 'content'<br>
	 *         attribute of the return JSON object.
	 * @throws BTiaoExp if process failed, must throw the excepion with <br>
	 *         an error code assigned in the exception object.
	 */
	protected abstract Object post(Object arg, Collection<String> attrList) throws BTiaoExp;
	
	/**
	 * del process.
	 * @param arg if you put @JsonCvtInfo on the implement method,<br>
	 *            such as @JsonCvtInfo(objClassName="com.btiao.user.BTiaoUser")<br>
	 *            then, you will get an object parameter of 'objClassName' type.<br>
	 *            otherwise, arg is null
	 * @return the return object will be converted to the 'content'<br>
	 *         attribute of the return JSON object.
	 * @throws BTiaoExp if process failed, must throw the excepion with <br>
	 *         an error code assigned in the exception object.
	 */
	protected abstract Object del(Object arg) throws BTiaoExp;
	
	/**
	 * do preprocess, ex: get argument from url.
	 */
	protected void pre() {
		
	}
	
	private Object cvtJson2Obj(OP op, JSONObject jo, Collection<String> attrList) 
	throws Exception {
		if (jo == null) {
			return null;
		}
		
		Method method = null;
		
		
		if (op == OP.post) {
			Class<?>[] parameterTypes = {Object.class, Collection.class};
			method = this.getClass().getDeclaredMethod(op.toString(), parameterTypes);
		} else {
			Class<?>[] parameterTypes = {Object.class};
			method = this.getClass().getDeclaredMethod(op.toString(), parameterTypes);
		}
		
		JsonCvtInfo annot = method.getAnnotation(JsonCvtInfo.class);
		if (annot == null) {
			return null;
		}
		
		String objClassName = annot.objClassName();
		Class<?> clz = this.getClass();
		//objclassname在web应用内，必须使用web应用的类加载器才能加载到此类
		Object obj = clz.getClassLoader().loadClass(objClassName).newInstance();
		new JSONConvert().json2obj(jo, obj, attrList);
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
		
		int errCode = ErrCode.SUCCESS;
		setRstOfJRO(jro, ErrCode.SUCCESS);
		
		try {
			Object contentRet = null;
			
			try {
				if (op == OP.get) {
					Form form = this.getReference().getQueryAsForm();
					opUserId = (String)form.getFirstValue(RestFilterBasicAuth.ARG_NAME_USER);
					contentRet = get(form);
				} else {
					JSONObject jao = null;
					if (arg != null) {
						jao = arg.getJsonObject();
					} else {
						throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
					}
					
					JSONObject opUsrInfo = (JSONObject)jao.get(RestFilterBasicAuth.OP_USER_INFO_NAME);
					opUserId = (String)opUsrInfo.get(RestFilterBasicAuth.ARG_NAME_USER);
					
					Collection<String> attrList = new ArrayList<String>();
					Object argObj = cvtJson2Obj(op, jao, attrList);
					if (op == OP.del) {
						contentRet = del(argObj);
					} else if (op == OP.post) {
						contentRet = post(argObj, attrList);
					} else if (op == OP.put) {
						contentRet = put(argObj);
					}
				}
			} catch (BTiaoExp e) {
				errCode = e.errNo;
				setRstOfJRO(jro, errCode);
				
				BTiaoLog.logExp(log, e);
			} catch (Throwable e) {
				errCode = ErrCode.UNKOWN_ERR;
				setRstOfJRO(jro, errCode);
				
				BTiaoLog.logExp(log, e);
			}
			
			JSONObject content = new JSONObject();
			jro.put("content", content);
			setContentOfJRO(content, contentRet);
		} catch (Exception e) {
			errCode = ErrCode.WRONG_PARAM;
			setRstOfJRO(jro, errCode);
			
			BTiaoLog.logExp(log, e);
		}

		if (errCode != ErrCode.SUCCESS) {
			if (errCode == ErrCode.OBJ_NOT_IN_INFO_MODEL) {
				this.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			} else {
				this.setStatus(Status.CLIENT_ERROR_EXPECTATION_FAILED);
			}
		}
		return new JsonRepresentation(jro);
	}
	
	/**
	 * the identity of the user who execute the HTTP method.
	 */
	protected String opUserId;
	
	protected Logger log = BTiaoLog.get();
}
