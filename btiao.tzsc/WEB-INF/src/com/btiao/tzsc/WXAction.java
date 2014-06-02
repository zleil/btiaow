package com.btiao.tzsc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WXAction extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3142857989535992028L;
	
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			_doPost(request, response);
		} catch (Throwable e) {
			MyLogger.get().error("process wx post error!", e);
		}
	}
	
	public void _doPost(HttpServletRequest request,
			HttpServletResponse response) {	
		String xmlStr = null;
		
		try {
			int size = request.getContentLength();
			ServletInputStream input = request.getInputStream();
			
			MyLogger.get().debug("size="+size);
			
			MyLogger.get().debug("str="+request.getContentType());
			
			
			byte[] buffer = new byte[size];
			input.read(buffer,0,1);
			
			xmlStr = new String(buffer, "UTF-8");
			
			MyLogger.get().debug("receive wxmsg:\n" + xmlStr);
			
			WXMsg msg = WXMsgFactory.gen(xmlStr);
			if (msg != null) {		
				new WXMsgProcessor().proc(msg, response);
			} else {
				MyLogger.get().warn("can't parse wxmsg");
			}
		} catch (Exception e) {
			MyLogger.get().error("process wx post error!\n"+xmlStr, e);
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
		String sig = request.getParameter("signature");
		String tm = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		
		String echostr = request.getParameter("echostr");
		String token = "cqzytsyrjnnjdwiloveurebecca";
		
		StringBuilder sb = new StringBuilder();
		String[] beSort = {tm, nonce, token};
		Arrays.sort(beSort);
		sb.append(beSort[0]);
		sb.append(beSort[1]);
		sb.append(beSort[2]);
		
		String sha1str = Sha1.doit(sb.toString());
		
		String record = "\n\tsignature="+sig +
				"\n\ttimestamp="+tm +
				"\n\tnonce="+nonce +
				"\n\techostr="+echostr +
				"\n\n\ttosha1="+sb.toString() +
				"\n\tresult sha1="+sha1str;
		
		MyLogger.get().info(record);
		
		PrintWriter out;
		try {
			out = response.getWriter();
			
			if (sig.equals(sha1str)) {
				out.print(echostr);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
