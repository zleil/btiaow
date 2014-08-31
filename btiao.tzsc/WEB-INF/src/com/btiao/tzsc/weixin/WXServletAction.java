package com.btiao.tzsc.weixin;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.Util;

public class WXServletAction extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3142857989535992028L;
	
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Util.logAccess(request, "weixin-cmd-process");
			
			if (!isValidReq(request)) {
				MyLogger.getAttackLog().warn("weixin-cmd-process: found a invalid request!!!");
				return;
			}
			
			_doPost(request, response);
			
			MyLogger.get().info("weixin-cmd-process: end");
		} catch (Throwable e) {
			MyLogger.get().error("weixin-cmd-process: error", e);
		}
	}
	
	public void _doPost(HttpServletRequest request,
			HttpServletResponse response) {	
		String xmlStr = null;
		
		try {
			String areaIdStr = getAreaId(request.getRequestURI());
			long areaId = Long.parseLong(areaIdStr);
			
			InputStream bf = request.getInputStream();
			int size = request.getContentLength();
			MyLogger.get().debug("char-encode="+request.getCharacterEncoding());
			MyLogger.get().debug("size="+size);
			MyLogger.get().debug("str="+request.getContentType());
			
			xmlStr = Util.getHttpEntityStr(bf, size, "UTF-8");
			
			WXMsg msg = WXMsgFactory.gen(xmlStr);
			if (msg != null) {
				new WXMsgProcessor(areaId).proc(msg, response);
			} else {
				MyLogger.get().warn("can't parse wxmsg");
			}
		} catch (Exception e) {
			MyLogger.get().error("weixin-cmd-process: error!\n"+xmlStr, e);
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {	
		try {
			_doGet(request, response);
		} catch (Throwable e) {
			MyLogger.get().error("process wx get error!", e);
		}
	}

	public void _doGet(HttpServletRequest request,
			HttpServletResponse response) {
		Util.logAccess(request, "check-fromweixin");
		
		String echostr = request.getParameter("echostr");
		PrintWriter out;
		try {
			out = response.getWriter();
			
			String areaId = getAreaId(request.getRequestURI());
			MyLogger.getAccess().info("areaId=" + areaId + ",src-ip=" + request.getRemoteAddr());
			
			if (isValidReq(request)) {
				out.print(echostr);
				return;
			}
		} catch (IOException e) {
			MyLogger.get().warn("_doGet failed!", e);
		}
	}
	
	private boolean isValidReq(HttpServletRequest request) {
		String sig = request.getParameter("signature");
		String tm = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		
		String echostr = request.getParameter("echostr");
		String token = "cqzytsyrjnnjdwiloveurebecca";
		
		if (sig == null || tm == null || nonce == null) {
			String record = "\n\tsignature="+sig +
					"\n\ttimestamp="+tm +
					"\n\tnonce="+nonce +
					"\n\techostr="+echostr;
			
			MyLogger.getAttackLog().info(record);
			
			return false;
		}
		
		StringBuilder sb = new StringBuilder();
		String[] beSort = {tm, nonce, token};
		Arrays.sort(beSort);
		sb.append(beSort[0]);
		sb.append(beSort[1]);
		sb.append(beSort[2]);
		
		String sha1str = Sha1.doit(sb.toString());
		
		if (sig.equals(sha1str)) {
			return true;
		} else {
			String record = "\n\tsignature="+sig +
					"\n\ttimestamp="+tm +
					"\n\tnonce="+nonce +
					"\n\techostr="+echostr +
					"\n\n\ttosha1="+sb.toString() +
					"\n\tresult sha1="+sha1str;
			
			MyLogger.getAttackLog().info(record);
			return false;
		}
	}
	
	private String getAreaId(String uri) {
		return uri.substring(uri.lastIndexOf('/') + 1);
	}
}
