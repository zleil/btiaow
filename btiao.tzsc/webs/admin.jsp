<%@ page import="com.btiao.tzsc.service.*"%>

<%@ page contentType="text/html; charset=UTF-8"  language="java" %>

<!DOCTYPE html> 
<html>
<head>
	<title>便条网 - 跳蚤市场 - 管理平台</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<link rel="stylesheet" href="jm/jquery.mobile-1.4.3.min.css" />
	<script src="jm/jquery-2.1.1.min.js"></script>
	<script src="jm/jquery.mobile-1.4.3.min.js"></script>
	<script src="admin.js"></script>
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

<body>
<div data-role="page">
	<div role="main" class="ui-content">
		<div data-role="header" data-position="fixed">
			<h1>共 <span id="total" style="font:bold"></span> 个用户待审批</h1>
		</div><!-- /header -->
		
		<ul data-role="listview" id="usrlist">
		  <li><a href="#operatePg" onclick="setOpPanel('usrid')" data-rel="dialog"></a></li>
		</ul>
	</div><!-- /content -->

	<div data-theme="a" data-role="footer" data-position="fixed">
		<h4>@Copyright 便条科技有限公司.</h4>
	</div><!-- /footer -->
</div>
<div data-role="popup" id="commonTip">
</div>
<div data-role="page" id="operatePg">
	<div role="main" class="ui-content">
		<a href="#" class="ui-btn ui-corner-all" id="telId">电话：<span id="telDesc"></span></a>
		<a href="#" data-rel="back" onclick="disagree();" class="ui-btn ui-corner-all">不同意</a>
		<a href="#" data-rel="back" onclick="approve();" class="ui-btn ui-corner-all">同意</a>
	</div><!-- /content -->
</div>
<script type="txt/javascript">
<%
out.println("var usrs='"+ComDataMgr.instance("UserInfo", 65537).getall()+"';");
%>
</script>
</body>
</html>