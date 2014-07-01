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
	
	public static String URI = "http://182.92.81.56/btiao/tzsc/wx_dispDetail/";

	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {	
		try {
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
			String areaIdStr = getAreaId(request.getRequestURI());
			long areaId = Long.parseLong(areaIdStr);
			
			MyLogger.getAccess().info("do wx dispDetail: areaId=" + areaIdStr + ",src-ip=" + request.getRemoteAddr());
			
			long stateId = Long.parseLong(stateIdStr);
			
			MyLogger.getAccess().info("stateId="+stateId);
			
			response.setCharacterEncoding("UTF-8");
			
			out = response.getWriter();
			
			display(areaId, stateId, out);
		} catch (IOException e) {
			MyLogger.get().warn("process wx_dispdetail _doGet failed!", e);
		}
	}
	
	private void display(long areaId, long stateId, PrintWriter out) {
		State state = StateMgr.instance(areaId).getState(stateId);
		
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE HTML>");
		sb.append("<html><head>");
		sb.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">");
		sb.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0\">");
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
			
			sb.append("<h2>");
			
			String timeStr = "";
			long curTime = System.currentTimeMillis();
			float hours = (curTime - state.publishTime)/(3600000);
			if (hours < 1) {
				int minites = (int)((curTime - state.publishTime)/(60000));
				if (minites <= 0) {
					timeStr = Tip.get().rightNow;
				} else {
					timeStr = "" + (int)minites + Tip.get().minutesBefore;
				}
				
			} else if (hours < 24) {
				timeStr = "" + (int)hours + Tip.get().hoursBefore;
			} else {
				Date date = new Date(state.publishTime);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				timeStr = formatter.format(date);
			}
			
			sb.append("<p>发布时间：");
			sb.append(timeStr);
			sb.append("</p>");
			
			//sb.append("<span><a href=\"javascript:viewProfile();\">&nbsp;&nbsp;关注此跳蚤市场</a></span>");
			
			String phone = state.getPhoneNum();
			sb.append("<a href=\"tel:"+ phone +"\">点击电话交流("+phone+")</a>");
			sb.append("<p class=\"note\">声明：请自行确认物品，本跳蚤市场暂时不做任何承诺与保证。</p>");
			
			sb.append("</h2>");
			
			for (int i=1; i<state.infos.size(); ++i) {
				State.Info info = state.infos.get(i);
				
				if (info.t == State.Info.MsgType.text) {
					sb.append("<p>");
					sb.append(info.content);
					sb.append("</p>");
				} else if (info.t == State.Info.MsgType.pic) {
					sb.append("<img src=\"");
					sb.append(info.content);
					sb.append("\"/>");
				}
			}
			
			sb.append("</div>");
			sb.append("<div id=\"rightCol\">&nbsp;</div>");
		} else {
			sb.append("对不起，您访问的物品可能已经被交换了：-） 再看看别的物品吧～～～");
		}
		
		sb.append("</body></html>");
		
		out.write(sb.toString());
	}
	
	private String getAreaId(String uri) {
		return uri.substring(uri.lastIndexOf('/') + 1);
	}
}
