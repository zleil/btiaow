package com.btiao.tzsc.weixin;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.service.ComDataMgr;
import com.btiao.tzsc.service.MetaDataId;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.SessionMgr;
import com.btiao.tzsc.service.State;
import com.btiao.tzsc.service.StateMgr;
import com.btiao.tzsc.service.UserInfo;

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
		String tip = URLEncoder.encode("本微信账号已登记了房屋7#2507，请确认是否需要重新登记", "UTF-8");
		System.out.println(tip);
	}
	
	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {	
		String code = request.getParameter("code");
		if (code == null) {
			return;
		}
		
		PrintWriter out;
		try {
			String areaIdStr = getAreaId(request.getRequestURI());
			long areaId = Long.parseLong(areaIdStr);
			
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			
			WXUserInfo uinfo = new WXUserInfo();
			if (code.equals("zleil")) {
				//for pc test
				
				uinfo.openId = "zleil";
				uinfo.accesToken = "zleil";
				
				List<State> states = StateMgr.instance(areaId).getAllStateByUserName("zleil");
				if (states == null || states.size() == 0) {
					State state = new State("zleil");
					state.areaId = areaId;
					State.Info info = new State.Info(State.Info.MsgType.text, "test tile");
					state.infos.add(info);
					State.Info info2 = new State.Info(State.Info.MsgType.phone, "@13812345678");
					state.infos.add(info2);
					State.Info info3 = new State.Info(State.Info.MsgType.pic, "http://");
					state.infos.add(info3);
					
					StateMgr.instance(areaId).addState("zleil", state);
					
					State state2 = new State("zleil");
					state2.areaId = areaId;
					state2.infos.add(info);
					state2.infos.add(info2);
					state2.infos.add(info3);
					
					StateMgr.instance(areaId).addState("zleil", state2);
				}
			} else {
				int retCode = new WXApi().getUserIdFromCode(code, uinfo);
				if (retCode != 0) {
					MyLogger.getAttackLog().warn("process wx_managemine failed to get uinfo, errcode="+retCode+"\ncode="+code+"\nfrom="+request.getRemoteAddr());
					return;
				}
			}
			
			SessionMgr.instance(areaId).addSession(uinfo.openId, uinfo.accesToken);
			
			String act = request.getParameter("act");
			
			//可以增加一个机制
			request.setAttribute("areaId", areaId);
			setAccessToken(response, areaId, uinfo.openId, uinfo.accesToken);
			
			MyLogger.get().info("do a redirect!");
			
			if (act == null) {
				genManageMinePage(areaId, uinfo, out);
			} else if (act != null && act.equals("dengji")) {
				UserInfo reguinfo = ComDataMgr.<UserInfo>instance(MetaDataId.dengji, areaId).get(uinfo.openId);
				if (reguinfo.usrId.equals(uinfo.openId)) {
					MyLogger.get().info("querystring is :\n"+request.getQueryString());
					String tip = URLEncoder.encode("本微信账号已登记了房屋"+reguinfo.homeId+"，请确认是否需要重新登记？", "UTF-8");
					String yesUrl = URLEncoder.encode("tzscdj.html", "UTF-8");
					response.sendRedirect("../webs/htmlconfirmtip.jsp?tip="+tip+"&yesurl="+yesUrl);
				} else {
					response.sendRedirect("../webs/tzscdj.html");
				}
			} else if (act.equals("admin")) {
				response.sendRedirect("../webs/admin.jsp");
			} else {
				MyLogger.getAttackLog().warn("process wx_managemine: unkown act="+act+"\nfrom="+request.getRemoteAddr());
				return;
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
	
	private void setAccessToken(HttpServletResponse response, long areaId, String usrId, String token) {
		int timeout = 3600;
		String domain = "182.92.81.56";
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
	}
	
	private void genManageMinePage(long areaId, WXUserInfo uinfo, PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE HTML>");
		sb.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">");
		sb.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,initial-scale=1,maximum-scale=1,user-scalable=no\">");
		sb.append("<link rel=\"stylesheet\" href=\"../webs/a.css\" media=\"all\">");
		
		sb.append("<script type=\"text/javascript\" src=\"../webs/jquery.js\"></script>");
		sb.append("<script type=\"text/javascript\" src=\"../webs/managmine.js\"></script>");
		
		sb.append("<script type=\"text/javascript\">");
		sb.append("var uid=\""+uinfo.openId+"\";");
		sb.append("var areaId="+areaId+";");
		sb.append("var token=\""+uinfo.accesToken+"\";");
		sb.append("</script>");
		
		List<State> states = StateMgr.instance(areaId).getAllStateByUserName(uinfo.openId);
		
		sb.append("<p>物品数：<span id=\"stateNum\">"+states.size()+"</span></p>");
		
		for (State state : states) {
			sb.append("<div class=\"perState\" id=\"state_"+state.id+"\">");
			sb.append("<div><div class=\"perSateTitle\">"+state.infos.get(0).content+"</div>");
			String picurl = state.getFirstPicUrl();
			if (picurl != null && !picurl.equals("")) {
				String stateUrl = WXServletDispDetail.dispDetailURI+state.areaId+"?stateId=" + state.id;
				sb.append("<div class=\"perStatePic\"><a href=\""+stateUrl+"\"> <img src=\""+picurl+"\"></img></a></div>");
			}
			sb.append("</div><div class=\"btns\">");
			sb.append("<button class=\"switched\" onclick=\"act_switched("+state.id+")\">已成交</button>");
			sb.append("<button class=\"cancel\" onclick=\"act_canceled("+state.id+")\">不卖了</button>");
			sb.append("</div>");
			sb.append("</div>");
		}
		
		out.write(sb.toString());
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
