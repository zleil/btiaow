package com.btiao.base.oif.restlet;

import org.json.JSONObject;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.routing.Filter;

import com.btiao.base.exp.ErrCode;
import com.btiao.base.service.BTiaoServiceMgr;
import com.btiao.base.service.UserService;

/**
 * basic authorization implement.<br>
 * just check whether the token of specified user-id is valid.<br>
 * @author zleil
 *
 */
public class RestFilterBasicAuth extends Filter {
	static public final String OP_USER_INFO_NAME = "__opUsrInfo";
	/**
	 * all restfull operation must contain userId info.<br>
	 * when it's GET method, uId is contained in URI.<br>
	 * otherwise, uId is contained in the JSON.<br>
	 */
	static public final String ARG_NAME_USER = "uId";
	
	/**
	 * all restfull operation must contain token info.<br>
	 * when it's GET method, uId is contained in URI.<br>
	 * otherwise, info is contained in the JSON.<br>
	 */
	static public final String ARG_NAME_TOKEN = "token";
	
	protected int beforeHandle(Request request, Response response) {
		if (request.getMethod() == Method.GET) {
			return doHandleGet(request, response);
		} else {
			return doHandleOther(request, response);
		}
	}
	
	protected int doHandleGet(Request request, Response response) {
		Form form = request.getResourceRef().getQueryAsForm();
		
		String uId = (String)form.getFirstValue(OP_USER_INFO_NAME+"["+ARG_NAME_USER+"]");
		String token = (String)form.getFirstValue(OP_USER_INFO_NAME+"["+ARG_NAME_TOKEN+"]");
		
		return commonProcess(uId, token, request, response);
	}
	
	protected int doHandleOther(Request request, Response response) {
		if (isLoginOp(request)) {
			return Filter.CONTINUE;
		}
		
		try {
			JsonRepresentation jr = new JsonRepresentation(request.getEntity());
			JSONObject opUsrInfo = null;
			
			JSONObject jao = jr.getJsonObject();
			
			request.setEntity(new JsonRepresentation(jao));
			
			opUsrInfo = (JSONObject)jao.get(OP_USER_INFO_NAME);
			
			String uId = (String)opUsrInfo.get(ARG_NAME_USER);
			String token = (String)opUsrInfo.get(ARG_NAME_TOKEN);
			
			return commonProcess(uId, token, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			setFaildRsp(response);
			return Filter.STOP;
		}
	}
	
	private boolean isLoginOp(Request request) {
		if (request.getMethod() != Method.PUT) {
			return false;
		}
		
		String uriStr = request.getResourceRef().toUri().toString();
		//must match all the path elment, 
		//otherwise there may increase the chance of security fault.
		if (!(uriStr.matches("^http://[^/]*/btiao/usrmgr/users/[^/]*/auth/0$")) &&
			!(uriStr.matches("^http://[^/]*/btiao/usrmgr/fusers/[^/]*/auth/0$"))) {
			return false;
		}
		
		return true;
	}
	
	private int commonProcess(String uId, String token, 
			Request request, Response response) {
		if (uId == null || token == null) {
			setFaildRsp(response);
			return Filter.STOP;
		}
		
		BTiaoServiceMgr svcMgr = BTiaoServiceMgr.instance();
		UserService usrSvc = (UserService)svcMgr.getService(UserService.class.getName());
		if (!usrSvc.baseAuth(uId, token)) {
			setFaildRsp(response);
			return Filter.STOP;
		}
		
		return Filter.CONTINUE;
	}
	
	private void setFaildRsp(Response response) {
		JSONObject jro = new JSONObject();
		ResBTBase.setRstOfJRO(jro, ErrCode.MAY_LOGOUTED);
		response.setEntity(new JsonRepresentation(jro));
		response.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
	}
}
