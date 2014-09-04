<!DOCTYPE html>
<%
//url: .jsp?tip=xxx[&hasyes=][&hasno=][&yesurl=xxx][&nourl=xxx]
//withoutNoBt参数表示是否不用“否”按钮
%>

<%@ page import="java.net.URLDecoder"%>

<%@ page contentType="text/html; charset=UTF-8"  language="java" %>

<html>
<head>
	<title>便条网 - 跳蚤市场</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<link rel="stylesheet" href="jm/jquery.mobile-1.4.3.min.css" />
	<script src="jm/jquery-2.1.1.min.js"></script>
	<script src="jm/jquery.mobile-1.4.3.min.js"></script>
</head>
<body>
<div data-role="page" id="confirmPg">
	<div data-role="header">
		<h2>提示</h2>
	</div><!-- /header -->
	<div role="main" class="ui-content">
		<p id="commonTip"></p>
	</div>
	<div role="main" class="ui-content">
		<%
		boolean hasno = request.getParameter("hasno") != null;
		boolean hasyes = request.getParameter("hasyes") != null;
		%>
		<a href="#" style="display:<%=hasno?"block":"none"%>" class="ui-btn ui-corner-all" id="no_bt">否</a>
		<a href="#" style="display:<%=hasyes?"block":"none"%>" class="ui-btn ui-corner-all" id="yes_bt">是</a>
	</div>
</div>
<script type="text/javascript">
var oldLoad = window.onload;
window.onload = function() {
	if (typeof(oldLoad) == "undefined") oldLoad();
	
	<%
	String tip=request.getParameter("tip");
	tip = URLDecoder.decode(tip, "UTF-8");
	out.print("var tip = '"+tip+"';");
	
	String yesurl=request.getParameter("yesurl");
	if (yesurl != null && !yesurl.isEmpty()) {
		yesurl = URLDecoder.decode(yesurl, "utf-8");
		out.print("var yesurl = '"+yesurl+"';");
	}
	
	String nourl=request.getParameter("nourl");
	if (nourl != null && !nourl.isEmpty()) {
		nourl = URLDecoder.decode(nourl, "utf-8");
		out.print("var nourl = '"+nourl+"';");
	}
	%>
	
	$("#commonTip").text(tip);
	
	$("#no_bt").click(function(){
		if (typeof(nourl) == "undefined") {
			if (typeof(WeixinJSBridge) != "undefined") {
				WeixinJSBridge.call("closeWindow");
			} else {
				window.close();
			}
		} else {
			window.location.href = nourl;
		}
	});
	
	$("#yes_bt").click(function(){
		if (typeof(yesurl) == "undefined") {
			if (typeof(WeixinJSBridge) != "undefined") {
				WeixinJSBridge.call("closeWindow");
			} else {
				window.close();
			}
		} else {
			window.location.href = yesurl;
		}
	});
}
</script>
</body>
</html>