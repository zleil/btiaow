<%@ page import="com.btiao.tzsc.service.*"%>
<%@ page import="com.btiao.tzsc.restful.*"%>
<%@ page import="java.util.List"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>

<%@ page contentType="text/html; charset=UTF-8"  language="java" %>

<!DOCTYPE html> 
<html>
<head>
	<title>便条网 - 跳蚤市场 - 我的物品</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<link rel="stylesheet" href="jm/jquery.mobile-1.4.3.min.css" />
	<link rel="stylesheet" href="dispstate.css" />
	<script src="jm/jquery-2.1.1.min.js"></script>
	<script src="jm/jquery.mobile-1.4.3.min.js"></script>
	<script src="dispstate.js"></script>
	<style type="text/css">
		.tzscdj-selgrid{
			font-size:1.2em;
			line-height:2.6em;
			text-align:center;
		}
		#selTelId{
			margin-bottom:1em;
		}
	</style>
</head>
<script type="text/javascript">
<%
TZSCCookieInfo cinfo = Api.getCookieInfo(request);
if (!cinfo.isValidSession()) {
	String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy,"UTF-8")+"&hasyes=";
	((HttpServletResponse)response).sendRedirect(newUrl);
	return;
}

long stateId = 0;
String stateIdStr = request.getParameter("stateId");
try {
	stateId = Long.parseLong(stateIdStr);
} catch (Exception e) {
	String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy,"UTF-8")+"&hasyes=";
	((HttpServletResponse)response).sendRedirect(newUrl);
	return;
}
WPState state = StateMgr.instance(cinfo.areaId).getState(stateId);
if (state == null) {
	String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy,"UTF-8")+"&hasyes=";
	((HttpServletResponse)response).sendRedirect(newUrl);
	return;
}
out.println("var dispdetail.state="+StateMgr.instance(cinfo.areaId).getState(stateId)+";");
%>
</script>
<body>
<div data-role="page">	
	<div role="main" class="ui-content">
		<%
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
		
		out.print("<p class=\"pos\">当前位置：便条网 -> "+AreaMgr.instance().getArea(state.areaId).desc+" -> 跳蚤市场</p>");
		
		out.print("<p class=\"fabuTime\">发布时间：");
		out.print(timeStr);
		out.print("</p>");
		out.print("<div class=\"line\">&nbsp;</div>");
		
		String title = state.getInfos().get(0).content;
		out.print("<p>");
		out.print(title);
		out.print("</p>");
		
		//sb.append("<span><a href=\"javascript:viewProfile();\">&nbsp;&nbsp;关注此跳蚤市场</a></span>");
		
		String phone = state.getPhoneNum();
		out.println("<div class=\"bottom\"></div>");
		out.println("<a class=\"tel\" href=\"tel:"+ phone +"\">"+phone+"</a>");
		
		for (int i=1; i<state.getInfos().size(); ++i) {
			WPState.Info info = state.getInfos().get(i);
			
			if (info.t == WPState.Info.MsgType.text) {
				out.println("<p>");
				out.println(info.content);
				out.println("</p>");
			} else if (info.t == WPState.Info.MsgType.pic && info.content != null && !info.content.equals("")) {
				out.println("<div class=\"image\"><img src=\"");
				out.println(info.content);
				out.println("\"/></div>");
			}
		}
		%>
		<p class="note">免责声明：小伙伴们，在交易前请认真核对好物品信息与价格，便条网仅负责给买卖双方搭建一个信息传递的桥梁，我们不清楚物品质量与价格是否符合~</p>
		<p class="copyright">@Copyright 便条科技有限公司</p>
	</div>
</div>
<div data-role="popup" id="commonTip">
</div>
</body>
</html>