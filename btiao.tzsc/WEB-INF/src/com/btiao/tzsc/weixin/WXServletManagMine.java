package com.btiao.tzsc.weixin;

import java.io.PrintWriter;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.State;
import com.btiao.tzsc.service.StateMgr;

public class WXServletManagMine extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7483127017720502610L;

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
			int retCode = new WXApi().getUserIdFromCode(code, uinfo);
			if (retCode != 0) {
				MyLogger.getAttackLog().warn("process wx_managemine failed to get uinfo, errcode="+retCode+"\ncode="+code+"\nfrom="+request.getRemoteAddr());
				return;
			}
			
			genManageMinePage(areaId, uinfo, out);
		} catch (Throwable e) {
			MyLogger.get().warn("process wx_managemine _doGet failed!", e);
		}
	}
	
	private void genManageMinePage(long areaId, WXUserInfo uinfo, PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE HTML>");
		sb.append("<html><head>");
		sb.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">");
		sb.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0\">");
		sb.append("<link rel=\"stylesheet\" href=\"../webs/a.css\" media=\"all\">");
		sb.append("</head><body>");
		
		sb.append("<script type=\"text/javascript\" src=\"../webs/jquery.js\"></script>");
		sb.append("<script type=\"text/javascript\" src=\"../webs/managmine.js\"></script>");
		
		sb.append("<script type=\"text/javascript\">");
		sb.append("var uid="+uinfo.openId+";");
		sb.append("var areaId="+areaId+";");
		sb.append("var token="+uinfo.accesToken+";");
		sb.append("</script>");

		List<State> states = StateMgr.instance(areaId).getAllStateByUserName(uinfo.openId);
		for (State state : states) {
			sb.append("<div class=\"perState\" id=\"state_"+state.id+"\">");
			sb.append("<ul>");
			sb.append("<li class=\"perSateTitle\">"+state.infos.get(0).content+"</li>");
			String picurl = state.getFirstPicUrl();
			if (picurl != null && !picurl.equals("")) sb.append("<li class=\"perStatePic\"><img src=\""+picurl+"\"></img></li>");
			sb.append("</ul>");
			sb.append("<div>");
			sb.append("<button class=\"switched\" onclick=\"act_switched("+state.id+")\">已成交</button>");
			sb.append("<button class=\"cancel\" onclick=\"act_canceled("+state.id+")\">不买了</button>");
			sb.append("</div>");
			sb.append("</div>");
		}
		
		sb.append("</body></html>");
		
		out.write(sb.toString());
	}
	
	private String getAreaId(String uri) {
		return uri.substring(uri.lastIndexOf('/') + 1);
	}
}
