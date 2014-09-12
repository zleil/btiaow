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
import com.btiao.tzsc.service.GlobalParam;
import com.btiao.tzsc.service.MetaDataId;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.Question;
import com.btiao.tzsc.service.SessionMgr;
import com.btiao.tzsc.service.StateMgr;
import com.btiao.tzsc.service.UserInfo;
import com.btiao.tzsc.service.Util;
import com.btiao.tzsc.service.WPState;

/*
 * 1. request
 * url: /baseUrl/api
 * http entity:{act:xxx,data:...}
 * cookies: "usrId=xxx; accessToken=xxx; areaId=xxx; ...";
 * 
 * 2. response
 * {errcode:xxx;errdesc:xxx;data:...}
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
				(SessionMgr.instance(areaId)!= null && !SessionMgr.instance(areaId).isOnlineUser(usrId, token))) {
				errorRsp(ErrCode.auth_failed, null, response);
				return;
			}
			
			int errcode = ErrCode.success;
			
			if (actType.equals("blockAllInput")) {
				GlobalParam.blockAllInput = true;
				errorRsp(0, "", response);
				return;
			}
			if (actType.equals("stateChange")) {
				JSONObject argJso = (JSONObject)jso.get("data");
				long stateid = argJso.getLong("stateid");
				boolean isDel = argJso.getBoolean("isDel");
				errcode= StateMgr.instance(areaId).delOneStateById(stateid, isDel);
			} else if (actType.equals("dengji")) {
				JSONObject uinfoJo = (JSONObject)jso.get("data");
				final UserInfo uinfo = new UserInfo(usrId);
				uinfo.telId = uinfoJo.getString("telId");
				uinfo.homeId = uinfoJo.getString("homeId");
				uinfo.dengjiTime = System.currentTimeMillis();
				
				ComDataMgr<String,UserInfo> usrMgr = ComDataMgr.<String,UserInfo>instance(UserInfo.class.getSimpleName(), areaId);
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
					ComDataMgr<String,UserInfo> dm = ComDataMgr.<String,UserInfo>instance(MetaDataId.dengji, areaId);
					dm.add(uinfo.usrId, uinfo);
					
					//TODO 发送待审核的信息 
				}
			} else if (actType.equals("getalldengji")) {
				String usrs = ComDataMgr.<String,UserInfo>instance(MetaDataId.dengji,areaId).getall();
				errorRsp(ErrCode.success, null, usrs, response);
				return;
			} else if (actType.equals("approvedengji")) {
				JSONObject uinfoJo = (JSONObject)jso.get("data");
				String usrIdToApprove = uinfoJo.getString("usrId");
				UserInfo uinfoApproved = ComDataMgr.<String,UserInfo>instance(MetaDataId.dengji, areaId).remove(usrIdToApprove);
				uinfoApproved.dengjiApproveTime = System.currentTimeMillis();
				ComDataMgr.<String,UserInfo>instance(UserInfo.class.getSimpleName(), areaId).add(usrIdToApprove, uinfoApproved);
			} else if (actType.equals("question")) {
				JSONObject questionjso = (JSONObject)jso.get("data");
				long stateId = questionjso.getLong("stateId");
				String statement = questionjso.getString("statement");
				
				Question qs = new Question(stateId, usrId, statement);
				
				ComDataMgr<Long,Question> qsmgr = ComDataMgr.<Long,Question>instance(WPState.getQuestionPersistFileName(stateId));
				qsmgr.add(qs.id, qs);
			} else if (actType.equals("reply")) {
				JSONObject replyjso = (JSONObject)jso.get("data");
				long stateId = replyjso.getLong("stateId");
				long qsId = replyjso.getLong("qsId");
				String statement = replyjso.getString("statement");
				
				Question.Reply reply = new Question.Reply(usrId, statement);
				ComDataMgr<Long,Question> qsmgr = ComDataMgr.<Long,Question>instance(WPState.getQuestionPersistFileName(stateId));
				qsmgr.get(qsId).replies.add(reply);
			} else if (actType.equals("getallquestion")) {
				JSONObject replyjso = (JSONObject)jso.get("data");
				long stateId = replyjso.getLong("stateId");
				String dateStr = ComDataMgr.<Long,Question>instance(WPState.getQuestionPersistFileName(stateId)).getall();
				
				errorRsp(ErrCode.success, null, dateStr, response);
				return;
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
		sb.append(",\"errdesc\":\"");
		sb.append(errdesc != null ? errdesc : "");
		sb.append("\"");
		
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
