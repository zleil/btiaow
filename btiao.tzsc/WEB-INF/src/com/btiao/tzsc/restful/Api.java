package com.btiao.tzsc.restful;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.btiao.tzsc.service.ErrCode;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.SessionMgr;
import com.btiao.tzsc.service.StateMgr;

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
			
			byte[] buffer = new byte[size];
			int readed = 0;
			int times = 0;
			do {
				int avail = bf.available();
				MyLogger.get().debug("avail="+avail);
				
				int count = bf.read(buffer, 0, size-readed);
				
				if (count == -1) break;
				readed += count;
				if (readed >= size) break;
				
				try {
					Thread.sleep(500);
				} catch (Exception e) {}
			} while (times++ < 6);
			
			MyLogger.get().debug("readed="+readed + ",times="+times);
			
			String args = new String(buffer, "UTF-8");
			MyLogger.get().debug("receive restful api:\n" + args);
			
			JSONObject jso = new JSONObject(args);
			long areaId = jso.getLong("areaId");
			String wxuid = jso.getString("wxuid");
			String wxtoken = jso.getString("wxtoken");
			String actType = jso.getString("act");
			
			if (!SessionMgr.instance(areaId).isOnlineUser(wxuid, wxtoken)) {
				errorRsp(ErrCode.auth_failed, response);
				return;
			}
			
			int errcode = ErrCode.success;
			
			if (actType.equals("deleteState")) {
				long stateid = jso.getLong("stateid");
				errcode= StateMgr.instance(areaId).delOneStateById(stateid);
			} else if (actType.equals("successSwitch")) {
				long stateid = jso.getLong("stateid");
				errcode = StateMgr.instance(areaId).delOneStateById(stateid);
			} else {
				errcode = ErrCode.unkown_act_of_api;
			}
			
			errorRsp(errcode, response);
		} catch (Throwable e) {
			MyLogger.getAccess().error("process restful api error!", e);
		}
	}
	
	private void errorRsp(int i, HttpServletResponse response) throws Exception {
		response.setCharacterEncoding("UTF-8");
		
		OutputStream out = response.getOutputStream();
		out.write(("{\"errcode\":"+i+"}").getBytes());
	}
}
