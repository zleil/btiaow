package com.btiao.tzsc.weixin;

import java.io.IOException;
import java.io.PrintWriter;

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
		sb.append("<html><body>");
		if (state != null) {
			for (State.Info info : state.infos) {
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
		sb.append("");
		sb.append("</body></html>");
		
		out.write(sb.toString());
	}
	
	private String getAreaId(String uri) {
		return uri.substring(uri.lastIndexOf('/') + 1);
	}
}
