package com.btiao.tzsc.weixin;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.service.ComDataMgr;
import com.btiao.tzsc.service.GlobalParam;
import com.btiao.tzsc.service.MetaDataId;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.SessionMgr;
import com.btiao.tzsc.service.Tip;
import com.btiao.tzsc.service.UserInfo;
import com.btiao.tzsc.service.Util;

/**
 * http://182.92.81.56/btiao/tzsc/wx_managemine/65537?code=zleil&act=xxx
 * 
 * act = dengji | null
 * 
 * @author zleil
 *
 */
public class WXServletManagMine extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7483127017720502610L;
	
	static public void main(String[] args) throws Exception {
		String tip = URLEncoder.encode("本微信账号已登记了房屋7#2507，请确认是否需要重新登记？", "UTF-8");
		System.out.println(tip);
	}
	
	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		Util.logAccess(request, "weixin-to-web");
		
		String code = request.getParameter("code");
		if (code == null) {
			MyLogger.get().info("querystring is :\n"+request.getQueryString());
			String tip = "";
			try {
				tip = URLEncoder.encode("朋友，便条网->跳蚤市场 当前仅支持微信认证哦，请勾选“同意使用基本资料登陆此应用”，然后选择“允许”~", "UTF-8");
				String newUrl = "../webs/htmlconfirmtip.jsp?tip="+tip+"&hasyes=";
				redirect(newUrl, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return;
		}
		
		try {
			String areaIdStr = getAreaId(request.getRequestURI());
			long areaId = Long.parseLong(areaIdStr);
			
			response.setCharacterEncoding("UTF-8");
			
			WXUserInfo uinfo = new WXUserInfo();
			if (code.equals("zleil")) {
				//for pc test，在SessionMgr对象中已内置zleil这个特殊用户
				uinfo.openId = "zleil";
				uinfo.accesToken = request.getParameter("t");
				if (uinfo.accesToken == null) {
					uinfo.accesToken = "";
				}
			} else {
				int retCode = new WXApi().getUserIdFromCode(code, uinfo);
				if (retCode != 0) {
					MyLogger.getAttackLog().warn("process wx_managemine failed to get uinfo, errcode="+retCode+"\ncode="+code+"\nfrom="+request.getRemoteAddr());
					return;
				}
			}
			
			String act = request.getParameter("act");
			String newUrl = "";
			
			if (act == null || act.equals("managmine")) {
				newUrl = "../webs/managmine.jsp";
			} else if (act != null && act.equals("dengji")) {
				UserInfo reguinfo = ComDataMgr.<UserInfo>instance(MetaDataId.dengji, areaId).get(uinfo.openId);
				UserInfo reguinfo2 = ComDataMgr.<UserInfo>instance(UserInfo.class.getSimpleName(), areaId).get(uinfo.openId);
				if (reguinfo != null) {
					MyLogger.get().info("querystring is :\n"+request.getQueryString());
					String tip = URLEncoder.encode("本微信账号正申请登记房屋"+reguinfo.homeId+"，请确认是否需要重新申请登记？", "UTF-8");
					String yesUrl = URLEncoder.encode("tzscdj.html", "UTF-8");
					newUrl = "../webs/htmlconfirmtip.jsp?tip="+tip+"&hasyes="+"&hasno="+"&yesurl="+yesUrl;
				} else if (reguinfo2 != null) {
					if (reguinfo == null) reguinfo = reguinfo2;
					MyLogger.get().info("querystring is :\n"+request.getQueryString());
					String tip = URLEncoder.encode("本微信账号已登记房屋"+reguinfo.homeId+"，并且已审核通过，请确认是否需要重新申请登记？", "UTF-8");
					String yesUrl = URLEncoder.encode("tzscdj.html", "UTF-8");
					newUrl = "../webs/htmlconfirmtip.jsp?tip="+tip+"&hasyes="+"&hasno="+"&yesurl="+yesUrl;
				} else {
					newUrl = "../webs/tzscdj.html";
				}
			} else if (act.equals("admin")) {
				newUrl = "../webs/admin.jsp?areaId="+areaId;
			} else if (act.equals("adminstat")) {
				newUrl = "../webs/adminstat.jsp";
			} else if (act.equals("dispstate")) {
				String stateId = request.getParameter("stateId");
				if (stateId != null) {
					newUrl = "../webs/dispstate.jsp?stateId="+stateId+"&areaId="+areaId;
				} else {
					newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy,"UTF-8")+"&hasyes=";
				}
			} else {
				MyLogger.getAttackLog().warn("process wx_managemine: unkown act="+act+"\nfrom="+request.getRemoteAddr());
				return;
			}
			
			setAccessToken(response, areaId, uinfo.openId, uinfo.accesToken);
			SessionMgr.instance(areaId).addSession(uinfo.openId, uinfo.accesToken);
			try {
				redirect(newUrl, response);
			} catch (Exception e) {
				MyLogger.get().error("failed to do a redirect to:"+newUrl, e);
				SessionMgr.instance(areaId).removeSession(uinfo.openId, uinfo.accesToken);
			}
		} catch (Throwable e) {
			MyLogger.getAttackLog().warn("process wx_managemine _doGet failed!", e);
			try {
				response.sendRedirect("../webs/internalError.html");
			} catch (IOException e1) {
				MyLogger.getAttackLog().warn(e1);
			}
		}
	}
	
	private void redirect(String newUrl, HttpServletResponse response) throws Exception {
		MyLogger.get().info("do a redirect to:"+newUrl);
		response.sendRedirect(newUrl);
	}
	
	private void setAccessToken(HttpServletResponse response, long areaId, String usrId, String token) {
		int timeout = 3600;
		String domain = GlobalParam.btw_domain;
		String path = "/";
		
		Cookie cookie = new Cookie("accessToken", token);
		cookie.setMaxAge(timeout);
		cookie.setDomain(domain);
		cookie.setPath(path);
		response.addCookie(cookie);
		
		Cookie cookie2 = new Cookie("usrId", usrId);
		cookie2.setMaxAge(timeout);
		cookie2.setDomain(domain);
		cookie2.setPath(path);
		response.addCookie(cookie2);
		
		Cookie cookie3 = new Cookie("areaId", Long.toString(areaId));
		cookie3.setMaxAge(timeout);
		cookie3.setDomain(domain);
		cookie3.setPath(path);
		response.addCookie(cookie3);
		
		UserInfo reguinfo = ComDataMgr.<UserInfo>instance(UserInfo.class.getSimpleName(), areaId).get(usrId);
		if (reguinfo != null) {
			Cookie cookie4 = new Cookie("homeId", reguinfo.homeId);
			cookie4.setMaxAge(timeout);
			cookie4.setDomain(domain);
			cookie4.setPath(path);
			response.addCookie(cookie4);
		}
	}
	
	private String getAreaId(String uri) {
		MyLogger.get().info("uri="+uri);
		String t1 = uri.substring(uri.lastIndexOf("wx_managemine/") + "wx_managemine/".length());
		int i = t1.indexOf('/');
		if (i != -1) {
			return t1.substring(0, i);
		}
		
		i = t1.indexOf('?');
		if (i != -1) {
			return t1.substring(0, i);
		}
		
		return t1;
	}
}
