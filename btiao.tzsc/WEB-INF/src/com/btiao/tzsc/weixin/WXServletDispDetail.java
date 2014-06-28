package com.btiao.tzsc.weixin;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.State;
import com.btiao.tzsc.service.StateMgr;

public class WXServletDispDetail extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8378786285598298529L;

	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {	
		try {
			String areaId = getAreaId(request.getRequestURI());
			MyLogger.getAccess().info("do wx dispDetail: areaId=" + areaId + ",src-ip=" + request.getRemoteAddr());
			
			_doGet(request, response);
			
			MyLogger.getAccess().info("do wx dispDetail end");
		} catch (Throwable e) {
			MyLogger.getAccess().error("process wx_dispdetail error!", e);
		}
	}

	public void _doGet(HttpServletRequest request,
			HttpServletResponse response) {		
		String stateIdStr = request.getParameter("stateId");
		if (stateIdStr == null) {
			return;
		}
		
		PrintWriter out;
		try {
			long stateId = Long.parseLong(stateIdStr);
			
			MyLogger.getAccess().info("stateId="+stateId);
			
			response.setCharacterEncoding("UTF-8");
			
			out = response.getWriter();
			
			display(stateId, out);
		} catch (IOException e) {
			MyLogger.get().warn("process wx_dispdetail _doGet failed!", e);
		}
	}
	
	private void display(long stateId, PrintWriter out) {
		State state = StateMgr.instance().getState(stateId);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<html><head>");
		sb.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">");
		sb.append("<link rel=\"stylesheet\" href=\"../webs/a.css\" media=\"all\">");
		sb.append("</head><body>");
		if (state != null) {
			sb.append("<script type=\"text/javascript\"> function viewProfile() {"); 
			sb.append("typeof WeixinJSBridge != \"undefined\" && WeixinJSBridge.invoke && WeixinJSBridge.invoke(\"profile\", {");
			sb.append("username: user_name,");
			sb.append("scene: \"57\"");
			sb.append("});");
			sb.append("}</script>");
			
			sb.append("<div id=\"leftCol\">&nbsp;</div>");
			sb.append("<div id=\"conterCol\">");
			
			String title = state.infos.get(0).content;
			sb.append("<h1>");
			sb.append(title);
			sb.append("</h1>");
			
			sb.append("<h2><span><a href=\"javascript:viewProfile();\">关注此跳蚤市场</a></span>");
			
			Date date=new Date(state.publishTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timeStr = formatter.format(date);
			sb.append("&nbsp;&nbsp;<span>");
			sb.append(timeStr);
			sb.append("</span></h2>");
			
			for (int i=1; i<state.infos.size(); ++i) {
				State.Info info = state.infos.get(i);
				
				if (info.t == State.Info.MsgType.text) {
					sb.append("<p>");
					sb.append(info.content);
					sb.append("</p>");
				} else {
					sb.append("<img src=\"");
					sb.append(info.content);
					sb.append("\"/>");
				}
			}
		} else {
			sb.append("对不起，您访问的物品可能已经被交换了：-） 再看看别的物品吧～～～");
		}
		sb.append("</div>");
		sb.append("<div id=\"rightCol\">&nbsp;</div>");
		sb.append("");
		sb.append("</body></html>");
		
		out.write(sb.toString());
	}
	
	private String getAreaId(String uri) {
		return uri.substring(uri.lastIndexOf('/') + 1);
	}
}
