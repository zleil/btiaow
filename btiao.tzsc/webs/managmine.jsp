<%@ page import="com.btiao.tzsc.service.*"%>
<%@ page import="com.btiao.tzsc.restful.*"%>
<%@ page import="java.util.List"%>
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
	<script src="jm/jquery-2.1.1.min.js"></script>
	<script src="jm/jquery.mobile-1.4.3.min.js"></script>
	<script src="managmine.js"></script>
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
<%
TZSCCookieInfo cinfo = Api.getCookieInfo(request);
if (!cinfo.isValidSession()) {
	String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy,"UTF-8")+"&hasyes=";
	((HttpServletResponse)response).sendRedirect(newUrl);
}
List<WPState> states = StateMgr.instance(cinfo.areaId).getAllStateByUserName(cinfo.usrId);
%>
<body>
<div data-role="page">
	<div data-role="header" data-position="fixed">
		<h1>跳蚤市场 - 我的物品</h1>
	</div><!-- /header -->
	
	<div role="main" class="ui-content">
		<p>朋友，您共有 <span id="total" style="font:bold"><%=states.size()%></span> 个待交换物品</p>
		<div id="statesView"></div>
	</div>

	<div data-theme="a" data-role="footer" data-position="fixed">
		<h4>@Copyright 便条科技有限公司.</h4>
	</div><!-- /footer -->
</div>
<div data-role="popup" id="commonTip">
</div>

<script type="text/javascript">
<%
StringBuilder sb = new StringBuilder();
sb.append("[");
boolean first = true;
for (WPState state : states) {
	if (first) {
		first = false;
	} else {
		sb.append(",");
	}
	sb.append(state.toString());
}
sb.append("]");
out.println("managmine.states="+sb.toString()+";");
%>
</script>
</body>
</html>