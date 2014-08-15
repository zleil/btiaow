package com.btiao.tzsc.weixin;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.service.MyLogger;

/**
 * url: /baseUrl/act?actId=xxx[&other_args=other_values]{0,n}
 * argsjson:{...}
 * cookie: "accessToken=xxxtoken" ...;
 * @author zleil
 *
 */
public class WebAction extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2339187293033789649L;

	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {
		String actId = request.getParameter("actId");
		
		if (actId.equals("dengji")) {
			long areaId = -1;
			Cookie[] cookies= request.getCookies();
			for (Cookie cookie : cookies) {
				String n = cookie.getName();
				if (n.equals("areaId")) {
					String areaIdStr = cookie.getValue();
					areaId = Long.parseLong(areaIdStr);
					
					if (areaId < 0) {
						MyLogger.getAttackLog().warn("web act get a wrong dengji act,areaId"+",areaId="+areaIdStr+"\nfrom="+request.getRemoteAddr());
						return;
					}
					
					break;
				}
			}
			
			if (areaId < 0) {
				MyLogger.getAttackLog().warn("web act get a dengji act with no areaId in cookies\nfrom="+request.getRemoteAddr());
				return;
			}
			
			
			
		}
	}
}
