package com.btiao.tzsc.service;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

public class Util {
	static public String getHttpEntityStr(InputStream bf, int size, String charset) throws Exception {
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
		
		String str = new String(buffer, charset);
		
		MyLogger.get().debug("received http entity str:\n" + str);
		
		return str;
	}
	
	static public void logAccess(String accessDesc, HttpServletRequest request) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		sb.append("desc[");
		sb.append(accessDesc);
		sb.append("]\tfrom[");
		sb.append(request.getRemoteHost());
		sb.append(":");
		sb.append(request.getRemotePort());
		sb.append("]\turl[");
		sb.append(request.getRequestURL());
		sb.append("]");
		MyLogger.getAccess().warn(sb.toString());
	}
}
