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
	/**
	 * 
	 */
	private static final long serialVersionUID = 6718517957960630433L;

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			
			MyLogger.getAccess().info("do api, src-ip=" + request.getRemoteAddr());
			
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
			
			Cookie[] cookies = request.getCookies();
			for (Cookie cookie : cookies) {
				String n = cookie.getName();
				String v = cookie.getValue();
				if (n.equals("usrId")) {
					usrId = v;
				} else if (n.equals("accessToken")) {
					token = v;
				} else if (n.equals("areaId")) {
					areaId = Long.parseLong(v);
				}
			}
			
			try {
				areaId = jso.getLong("areaId");
			} catch (JSONException e) {
				;
			}

			String actType = jso.getString("act");
			
			if (!SessionMgr.instance(areaId).isOnlineUser(usrId, token)) {
				errorRsp(ErrCode.auth_failed, response);
				return;
			}
			
			int errcode = ErrCode.success;
			
			if (actType.equals("stateChange")) {
				JSONObject argJso = (JSONObject)jso.get("data");
				long stateid = argJso.getLong("stateid");
				errcode= StateMgr.instance(areaId).delOneStateById(stateid);
			} else if (actType.equals("dengji")) {
				JSONObject uinfoJo = (JSONObject)jso.get("data");
				UserInfo uinfo = new UserInfo(usrId);
				uinfo.telId = uinfoJo.getString("telId");
				uinfo.homeId = uinfoJo.getString("homeId");
				
				ComDataMgr<UserInfo> dm = ComDataMgr.<UserInfo>instance(MetaDataId.dengji, areaId);
				dm.add(uinfo.usrId, uinfo);
			} else if (actType.equals("getalldengji")) {
				String usrs = ComDataMgr.<UserInfo>instance(MetaDataId.dengji,areaId).getall();
				errorRsp(ErrCode.success, usrs, response);
			} else {
				errcode = ErrCode.unkown_act_of_api;
			}
			
			errorRsp(errcode, response);
		} catch (Throwable e) {
			MyLogger.getAccess().error("process restful api error!", e);
			try {
				errorRsp(ErrCode.internel_error, response);
			} catch (Exception e1) {
				MyLogger.getAccess().error(e1);
			}
		}
	}
	
	private void errorRsp(int i, HttpServletResponse response) throws Exception {
		errorRsp(i, null, response);
	}
	
	private void errorRsp(int i, String dataStr, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF-8");
		
		OutputStream out = response.getOutputStream();
		out.write(("{\"errcode\":"+i).getBytes());
		if (dataStr != null) {
			out.write(",\"data\":".getBytes());
			out.write(dataStr.getBytes());
		}
		out.write("}".getBytes());
	}
}
