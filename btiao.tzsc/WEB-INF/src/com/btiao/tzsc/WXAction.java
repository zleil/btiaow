package com.btiao.tzsc;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
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
