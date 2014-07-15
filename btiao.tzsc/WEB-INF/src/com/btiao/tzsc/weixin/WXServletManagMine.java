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
			
			WXOnlineUserMgr.instance(areaId).addUserInfo(uinfo);
			
			genManageMinePage(areaId, uinfo, out);
		} catch (Throwable e) {
			MyLogger.get().warn("process wx_managemine _doGet failed!", e);
		}
	}
	
	private void genManageMinePage(long areaId, WXUserInfo uinfo, PrintWriter out) {
		StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE HTML>");
		sb.append("<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">");
		sb.append("<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0\">");
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
		return uri.substring(uri.lastIndexOf('/') + 1);
	}
}
