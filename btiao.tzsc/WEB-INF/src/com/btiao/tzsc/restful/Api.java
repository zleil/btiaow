package com.btiao.tzsc.restful;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.btiao.tzsc.service.ComDataMgr;
import com.btiao.tzsc.service.ErrCode;
import com.btiao.tzsc.service.MetaDataId;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.SessionMgr;
import com.btiao.tzsc.service.StateMgr;
import com.btiao.tzsc.service.UserInfo;
import com.btiao.tzsc.service.Util;

/*
 * 1. request
 * url: /baseUrl/api
 * http entity:{act:xxx,data:...}
 * cookies: "usrId=xxx; accessToken=xxx; areaId=xxx; ...";
 * 
 * 2. response
 * {errcode:xxx;data:...}
 * @author zleil
 */
public class Api extends HttpServlet {
	static public TZSCCookieInfo getCookieInfo(HttpServletRequest request) {
		TZSCCookieInfo ret = new TZSCCookieInfo();
		
		Cookie[] cookies = request.getCookies();
		if (cookies != null) for (Cookie cookie : cookies) {
			String n = cookie.getName();
			String v = cookie.getValue();
			if (n.equals("usrId")) {
				ret.usrId = v;
			} else if (n.equals("accessToken")) {
				ret.token = v;
			} else if (n.equals("areaId")) {
				ret.areaId = Long.parseLong(v);
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6718517957960630433L;

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			
			Util.logAccess(request,"web-api");
			
			InputStream bf = request.getInputStream();
			int size = request.getContentLength();
			MyLogger.get().debug("char-encode="+request.getCharacterEncoding());
			MyLogger.get().debug("size="+size);
			MyLogger.get().debug("str="+request.getContentType());
			
			String args = Util.getHttpEntityStr(bf, size, "UTF-8");
			
			MyLogger.get().debug("receive restful api:\n" + args);
			JSONObject jso = new JSONObject(args);
			
			long areaId = 0;
			String usrId = null,token = null;
			
			TZSCCookieInfo cookieInfo = getCookieInfo(request);
			areaId = cookieInfo.areaId;
			usrId = cookieInfo.usrId;
			token = cookieInfo.token;
			
			try {
				areaId = jso.getLong("areaId");
			} catch (JSONException e) {
				;
			}

			String actType = jso.getString("act");
			
			MyLogger.get().info("api's base info:usrId="+usrId+",accessToken="+token+",areaId="+areaId);
			
			if (token == null || usrId == null || 
				!SessionMgr.instance(areaId).isOnlineUser(usrId, token)) {
				errorRsp(ErrCode.auth_failed, null, response);
				return;
			}
			
			int errcode = ErrCode.success;
			
			if (actType.equals("stateChange")) {
				JSONObject argJso = (JSONObject)jso.get("data");
				long stateid = argJso.getLong("stateid");
				errcode= StateMgr.instance(areaId).delOneStateById(stateid);
			} else if (actType.equals("dengji")) {
				JSONObject uinfoJo = (JSONObject)jso.get("data");
				final UserInfo uinfo = new UserInfo(usrId);
				uinfo.telId = uinfoJo.getString("telId");
				uinfo.homeId = uinfoJo.getString("homeId");
				uinfo.dengjiTime = System.currentTimeMillis();
				
				ComDataMgr<UserInfo> usrMgr = ComDataMgr.<UserInfo>instance(UserInfo.class.getSimpleName(), areaId);
				UserInfo existUinfo = usrMgr.scan(new ComDataMgr.IScan<UserInfo>() {

					@Override
					public boolean process(UserInfo d) {
						if (d.homeId != null && d.homeId.equals(uinfo.homeId)) {
							return true;
						}
						return false;
					}
				});
				if (existUinfo == null) {
					uinfo.dengjiApproveTime = uinfo.dengjiTime;
					usrMgr.add(uinfo.usrId, uinfo);
					
					//TODO 发送审核通过的信息
				} else {
					//1个homeId仅能被一个账号申请，此时需要管理员进行审核
					ComDataMgr<UserInfo> dm = ComDataMgr.<UserInfo>instance(MetaDataId.dengji, areaId);
					dm.add(uinfo.usrId, uinfo);
					
					//TODO 发送待审核的信息
				}
			} else if (actType.equals("getalldengji")) {
				String usrs = ComDataMgr.<UserInfo>instance(MetaDataId.dengji,areaId).getall();
				errorRsp(ErrCode.success, usrs, response);
				return;
			} else if (actType.equals("approvedengji")) {
				JSONObject uinfoJo = (JSONObject)jso.get("data");
				String usrIdToApprove = uinfoJo.getString("usrId");
				UserInfo uinfoApproved = ComDataMgr.<UserInfo>instance(MetaDataId.dengji, areaId).remove(usrIdToApprove);
				uinfoApproved.dengjiApproveTime = System.currentTimeMillis();
				ComDataMgr.<UserInfo>instance(UserInfo.class.getSimpleName(), areaId).add(usrIdToApprove, uinfoApproved);
			} else {
				errcode = ErrCode.unkown_act_of_api;
			}
			
			errorRsp(errcode, null, response);
		} catch (Throwable e) {
			MyLogger.getAccess().error("process restful api error!", e);
			try {
				errorRsp(ErrCode.internel_error, null, response);
			} catch (Exception e1) {
				MyLogger.getAccess().error(e1);
			}
		}
	}
	
	private void errorRsp(int i, String errdesc, HttpServletResponse response) throws Exception {
		errorRsp(i, errdesc, null, response);
	}
	
	private void errorRsp(int i, String errdesc, String dataStr, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF-8");
		
		OutputStream out = response.getOutputStream();
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\"errcode\":"+i);
		if (errdesc != null) {
			sb.append(",\"errdesc\":\"");
			sb.append(errdesc);
			sb.append("\"");
		}
		if (dataStr != null) {
			sb.append(",\"data\":");
			sb.append(dataStr);
		}
		sb.append("}");
		
		String rspstr = sb.toString();
		out.write(rspstr.getBytes());
		
		MyLogger.get().info("api rsp is:"+rspstr);
	}
}
