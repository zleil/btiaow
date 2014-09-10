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
	<title>便条网 - 跳蚤市场 - 物品信息</title>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<link rel="stylesheet" href="jm/jquery.mobile-1.4.3.min.css" />
	<script src="jm/jquery-2.1.1.min.js"></script>
	<script src="jm/jquery.mobile-1.4.3.min.js"></script>
	<script src="dispstate.js"></script>
	<style type="text/css">
@font-face {
  font-family: "FontAwesome";
  src: url("font/fontawesome-webfont.eot") format("embedded-opentype");
  src: url("font/fontawesome-webfont.eot") format("embedded-opentype"), url('font/fontawesome-webfont.woff') format('woff'), url('font/fontawesome-webfont.ttf') format('truetype'), url('font/fontawesome-webfont.svg') format('svg');
  font-weight: normal;
  font-style: normal;
}
p{
color:blue;
font-weight:700;
}
.line{
margin:1em 0 1em 0;
height:0.1em;
background-color:#cfcfcf;
width:100%;
border:solid 2px 0 0 0 #808080;
}
.headLine{
font-size:0.8em;
font-weight:bold;
color:#7f7f7f;
margin:0.2em 0 0.2em 0;
padding:0 0 0 0;
}
.image{
text-align:center;
}
img{
width:100%;
}
.fabuTime{

}
.bottom{
background-color:#f0f0f0;
width:100%;
height:0.5em;
position:fixed;
bottom:0em;
}
.tel:before{
content:"\f095";
font-family: FontAwesome;
left:5px;
position:absolute;
}
.tel:before{
content:"\f095";
font-family: FontAwesome;
left:5px;
position:absolute;
}
.talk:before {
content: "\f0e5";
font-family: FontAwesome;
left:5px;
position:absolute;
}
.tel,.talk{
color:white;
background-color:#ea5946;
background-image:none;
vertical-align:baseline;
text-decoration:none;
-webkit-border-radius:5px;
background-repeat: repeat;
text-align:center;
margin:0.2em 0 0.2em 0;
padding:0.2em 0 0.2em 0;
position:fixed;
bottom:0;
left:0;
display:block;
width:70%;
font-size:1.2em;
z-index:100;
}
.talk{
width:29.5%;
left:70.5%;
}
.tel:link,.talk:link,.tel:visited,.talk:visited{
color:#FFFFFF;
}
#footfake{
height:2.5em;
}
.note{
color:red;
margin:1em 0 1em 0;
font-size:0.8em;
}
.copyright{
margin:0 0 2em 0;
text-align:center;
background-color:#f0f0f0;
padding:1em 0 1em 0;
font-size:0.8em;
color:#5f5f5f;
}
	</style>
</head>
<script type="text/javascript">
var dispdetail = {};
dispdetail.state = {};

<%
long areaIdFromQueryStr = 0;
TZSCCookieInfo cinfo = Api.getCookieInfo(request);
if (!cinfo.isValidSession()) {
	String areaIdStrFromQueryStr = request.getParameter("areaId");
	if (areaIdStrFromQueryStr != null) {
		try {
			areaIdFromQueryStr = Long.parseLong(areaIdStrFromQueryStr);
		} catch (Exception e) {
			MyLogger.getAttackLog().warn("unkown access to dispstate.jsp, from:"+request.getRemoteAddr()+request.getQueryString());
			String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy+1,"UTF-8")+"&hasyes=";
			((HttpServletResponse)response).sendRedirect(newUrl);
			return;
		}
	}
	
	if (areaIdFromQueryStr == 0) {
		MyLogger.getAttackLog().warn("unkown access to dispstate.jsp, from:"+request.getRemoteAddr()+request.getQueryString());
		String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy+2,"UTF-8")+"&hasyes=";
		((HttpServletResponse)response).sendRedirect(newUrl);
		return;
	}
}

long stateId = 0;
String stateIdStr = request.getParameter("stateId");
try {
	stateId = Long.parseLong(stateIdStr);
} catch (Exception e) {
	MyLogger.getAttackLog().warn("unkown access to dispstate.jsp, from:"+request.getRemoteAddr()+request.getQueryString());
	
	String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy,"UTF-8")+"&hasyes=";
	((HttpServletResponse)response).sendRedirect(newUrl);
	return;
}

long areaId = cinfo.areaId;
if (areaIdFromQueryStr > 0) {
	areaId = areaIdFromQueryStr;
}

WPState state = StateMgr.instance(areaId).getState(stateId);
if (state == null) {
	MyLogger.getAttackLog().warn("unkown access to dispstate.jsp, from:"+request.getRemoteAddr()+request.getQueryString());
	
	String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().busy+4,"UTF-8")+"&hasyes=";
	((HttpServletResponse)response).sendRedirect(newUrl);
	return;
}
out.println("dispdetail.state="+StateMgr.instance(areaId).getState(stateId)+";");

if (!state.userId.equals(cinfo.usrId)) {
	++state.browseTimes;
}
%>

function addContact(areaId, cb) {
	if (typeof WeixinJSBridge == 'undefined') return false;
    WeixinJSBridge.invoke('addContact', {
        webtype: '1',
        username: '<%=AreaMgr.instance().get(areaId).wxId%>'
    }, function(d) {
        // 返回d.err_msg取值，d还有一个属性是err_desc
        // add_contact:cancel 用户取消
        // add_contact:fail　关注失败
        // add_contact:ok 关注成功
        // add_contact:added 已经关注
        alert(d.err_msg);
        typeof(cb) == "undefined" && cb(d.err_msg);
    });
}
</script>
<body>
<div data-role="page">	
	<div data-role="header">
		<h2>物品信息</h2>
	</div><!-- /header -->
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
				} else if (hours < 24*30) {
			timeStr = "" + (int)(hours/24) + Tip.get().daysBefore;
				} else {
			Date date = new Date(state.publishTime);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			timeStr = formatter.format(date);
				}
				
				UserInfo uinfoWpOwner = ComDataMgr.<Long,UserInfo>instance(UserInfo.class.getSimpleName(), state.areaId).get(state.userId);
				UserInfo uinfo = ComDataMgr.<Long,UserInfo>instance(UserInfo.class.getSimpleName(), state.areaId).get(cinfo.usrId);
				
				out.print("<p class=\"headLine\">卖主认证级别：");
				if (uinfoWpOwner.wuyeAuth) {
			out.print("<span style=\"color:green\">物业认证</span>");
				} else if (uinfoWpOwner.doorNumPicAuth) {
			out.print("<span style=\"color:green\">门牌认证</span>");
				} else {
			out.print("<span>普通认证</span>");
				}
				
				out.print("</p>");
				
				//经过门牌认证的用户，才能查看物品主人的门牌信息
				if ((uinfoWpOwner.wuyeAuth && (uinfo != null && uinfo.wuyeAuth)) ||
			(!uinfoWpOwner.wuyeAuth && uinfoWpOwner.doorNumPicAuth && (uinfo != null && (uinfo.doorNumPicAuth || uinfo.wuyeAuth))) ||
			(!uinfoWpOwner.wuyeAuth && !uinfoWpOwner.doorNumPicAuth && uinfo != null)) {
			out.print("<p class=\"headLine\">卖主的门牌号：");
			out.print(uinfoWpOwner.homeId);
			out.print("</p>");
				} else {
			out.print("<p class=\"headLine\">卖主的门牌号：");
			
			String reason = "需关注微信号";
			if (uinfoWpOwner.wuyeAuth) {
				reason = "需完成物业认证";
			} else if (uinfoWpOwner.doorNumPicAuth) {
				reason = "需完成门牌认证";
			}
			out.print(reason+"，才能查看，<a href=\"tzscdj.html\" target=\"_blank\" style=\"font:black\">我去注册!</a>");
			out.print("</p>");
				}
				
				out.print("<p class=\"headLine\">发布地点：便条网 -> <a href=\"#\" onclick=\"addContact("+areaId+")\">"+AreaMgr.instance().get(state.areaId).desc+" - 跳蚤市场 [关注]</a></p>");
				
				out.print("<p class=\"headLine\">发布时间：");
				out.print(timeStr);
				out.print("</p>");
				
				out.print("<p class=\"headLine\">浏览次数："+state.browseTimes+"</p>");
				
				out.print("<div class=\"line\">&nbsp;</div>");
				
				String title = state.getInfos().get(0).content;
				out.print("<p>");
				out.print(title);
				out.print("</p>");
				
				//sb.append("<span><a href=\"javascript:viewProfile();\">&nbsp;&nbsp;关注此跳蚤市场</a></span>");
				
				String phone = state.getPhoneNum();
				out.println("<div class=\"bottom\"></div>");
				out.println("<a class=\"tel\" href=\"tel:"+ phone +"\">"+phone+"</a>");
				out.println("<a class=\"talk\" href=\"#\">留言</a>");
				
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
		<div class="line">&nbsp;</div>
		
		<div class="replyArea">
			<div class="replyThread"></div>
			<div>查看更多留言</div>
		</div>
		
		<div class="line">&nbsp;</div>
		<p class="note">温馨提醒：小伙伴们，在交易前请认真核对好物品信息与价格，便条网目前仅负责给买卖双方搭建一个信息传递的桥梁，未对物品质量与价格的符合度做校验哦~</p>
		<p class="note">当前认证分三种：普通认证、门牌认证、物业认证</p>
		<p class="note">普通认证：在便条网成功登记门牌、手机信息</p>
		<p class="note">门牌认证：在便条网成功登记，并拍下自己的门的照片，门上的门牌号要与登记时一致</p>
		<p class="note">物业认证：在便条网成功登记，门牌号、手机号与物业中登记的一致</p>
		<!-- <p class="copyright">©2014 便条科技有限公司</p> -->
	</div>
	<div data-theme="a" data-role="footer">
		<h4>©2014 便条科技有限公司</h4>
	</div><!-- /footer -->
	<div id="footfake"><img/></div>
</div>
<div data-role="popup" id="commonTip">
</div>
</body>
</html>