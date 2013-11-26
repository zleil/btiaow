(function(){
var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

/**
 * 封装持久化接口，确保所有持久化到客户端的变量，必须在此类中定义
 */
function ClientPersist() {
	this.map = {};
	this.map["btiao.devUserId"] = true;
	this.map["btiao.orderDst"] = true;
	this.map["btiao.devPasswd"] = true;
}
ClientPersist.prototype.clearAll = function() {
	if (undefined == localStorage) return;
	
	for (var name in this.map) {
		delete localStorage[name];
	}
}
ClientPersist.prototype.get = function(name) {
	if (undefined == localStorage) return undefined;
	
	if (!this.map[name]) {
		throw Exception("get a variable not existing from client persist!");
	}
	
	return localStorage[name];
}
ClientPersist.prototype.set = function(name, value) {
	if (undefined == localStorage) return undefined;
	
	if (!this.map[name]) {
		throw Exception("get a variable not existing from client persist!");
	}
	
	localStorage[name] = value;
}

function Util() {}
Util.prototype.clearListViewData = function(selector) {
	$(selector).empty();
}
Util.prototype.getObj = function (url, func) {
	$.ajax({
		type: "GET",
		url: url,
		contentType: "application/json; charset=UTF-8",
		data: {__opUsrInfo: {uId: btiao.loginMgr.user, token: btiao.loginMgr.token}},
		success: func,
		error: function(xhq,errStatus,e){
			btiao.log.l(errStatus);
		}
	});
}
Util.prototype.getAllObj = function(url, func, num, lastId) {
	$.ajax({
		type: "GET",
		url: url,
		contentType: "application/json; charset=UTF-8",
		data: {
			__opUsrInfo: {uId: btiao.loginMgr.user, token: btiao.loginMgr.token},
			func: "normal",
			lastId: lastId,
			num: num
		},
		success: func,
		error: function(xhq,errStatus,e){
			btiao.log.l(errStatus);
		}
	});
}
Util.prototype.getId = function (idName, func) {
	$.ajax({
		type: "POST",
		url: "/btiao/product/getId/"+idName,
		contentType: "application/json; charset=UTF-8",
		data: '{\
			__opUsrInfo: {uId: "' + btiao.loginMgr.user + '", token: "' + btiao.loginMgr.token + '"} \
		}',
		success: function(d) {
			var idValue = d.content.nextValue;
			(func)(idValue);
		},
		error: function(xhq,errStatus,e){
			btiao.log.l(errStatus);
		}
	});
}
Util.prototype.getInnerJsonStr = function (obj) {
	var first = true;
	var r = "";
	
	for (attrName in obj) {
		if (!first) {
			r += ",";
		} else {
			first = false;
		}
		
		if (obj[attrName] instanceof Object) {
			r += attrName + ":{" + this.getInnerJsonStr(obj[attrName])+"}";
		} else {
			r += attrName + ":" + "\"" + obj[attrName] + "\"";
		}
	}
	return r;
}
Util.prototype.putOrPosObj = function(isPut, url, obj, func) {
	if (isPut) {
		this.putObj(url, obj, func);
	} else {
		this.postObj(url, obj, func);
	}
}
Util.prototype.putObj = function(url, obj, func) {
	$.ajax({
		type: "PUT",
		url: url,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo: {uId: "' + btiao.loginMgr.user + '", token: "' + btiao.loginMgr.token + '"},' +
			this.getInnerJsonStr(obj) +
		'}',
		success: func,
		error: function(httpReq, textStatus, errorThrow) {
			btiao.log.l("失败，请确认网络无故障，若无故障则可能是系统问题:"+textStatus);
		}
	});
}
Util.prototype.postObj = function(url, obj, func) {
	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo: {uId: "' + btiao.loginMgr.user + '", token: "' + btiao.loginMgr.token + '"},' +
			this.getInnerJsonStr(obj) +
		'}',
		success: func,
		error: function(httpReq, textStatus, errorThrow) {
			btiao.log.l("失败，请确认网络无故障，若无故障则可能是系统问题:"+textStatus);
		}
	});
}
/**
 * 整数价格呈现时，要缩小呈现小数价格。
 */
Util.prototype.decoratePrice = function(price) {
	return (price*1.0)/100;
}
/**
 * 系统仅存储整数价格，所以需要放大，再存，呈现时再缩小还原。
 */
Util.prototype.formIntPrice = function(price) {
	return parseInt(price*100);
}
Util.prototype.decorateTime = function(last) {
	var now = new Date().valueOf();
	var v = now - last;
	var minTotal = v/60000; minTotal = parseInt(minTotal);
	var min = minTotal%60; 
	var hourTotal = minTotal/60; hourTotal = parseInt(hourTotal);
	var hour = hourTotal%24;
	var dayTotal = hourTotal/24; dayTotal = parseInt(dayTotal);
	var day = dayTotal%30; 
	var monthTotal = dayTotal/30; monthTotal = parseInt(monthTotal);
	var month = monthTotal%12;
	var year = monthTotal/12;

	min = parseInt(min);
	hour = parseInt(hour);
	day = parseInt(day);
	month = parseInt(month);
	year = parseInt(year);
	
	var desc = "";
	if (year > 0) desc += (desc==""?"":" ") + year + "年";
	if (month >0) desc += (desc==""?"":" ") + month + "月";
	if (day >0) desc += (desc==""?"":" ") + day + "日";
	if (hour >0) desc += (desc==""?"":" ") + month + "小时";
	if (min >=0) desc += (desc==""?"":" ") + min + "分钟前";
	
	return desc;
}
Util.prototype.getSpecificUpperElm = function (elm, attrName) {
	var n = elm;
	var r = undefined;
	var i = 10;
	
	while (r == undefined && i-->0 && n != null) {
		r = $(n).attr(attrName);
		if (r != undefined) {
			return n;
		}
		
		n = n.parentNode;
	}
	
	return null;
}
Util.prototype.checkPriceValid = function (price) {
	var str = price.toString();
	if (str.match(/^[0-9]{1,8}$/) ||
			str.match(/^[0-9]{1,8}\.[0-9]$/) ||
			str.match(/^[0-9]{1,8}\.[0-9][0-9]$/)) {
		return true;
	} else {
		return false;
	}
}
Util.prototype.nullFunc = function(){}
Util.prototype.tipOver = function(info, errCode, closeFunc) {
	var txt = this.decorateTip(info, errCode);
	$("#btiaoOverTip").text(txt);
	$("#btiaoOverTip").popup("open");
	
	if (!!closeFunc) {
		$("#btiaoOverTip").on("popupafterclose", closeFunc);
	} else {
		$("#btiaoOverTip").on("popupafterclose", this.nullFunc);
	}	
}
Util.prototype.decorateTip = function(info, errCode) {
	var hasErr = (!!errCode && errCode != 0);
	var txt = "(@_@)" + 
		(hasErr ? "抱歉，" : "") +
		info + 
		(hasErr ? ("."+errCode+".") : "");
	
	return txt;
}
Util.prototype.tip = function(info, errCode) {
	var txt = this.decorateTip(info, errCode);
	
	$("#btiaoTip").text(txt);
	
	if (!!this.timerCloseTip) {
		clearTimeout(this.timerCloseTip);
	}
	
	$("#btiaoTip").popup("open");
	this.timerCloseTip = setTimeout(function(){
		this.timerCloseTip = undefined;
		$("#btiaoTip").popup("close");
	},2300);
}

function LoginMgr() {
	this.logined = false;
	this.user = undefined;
	this.friendUid = undefined;
	this.token = undefined;
}
LoginMgr.prototype.getDevUserId = function () {
	var devUserId = this.retriveDevId();
	if (!!devUserId) {
		return devUserId;
	}
	
	devUserId = this.genDevUserId();
	this.storeDevUserId(devUserId);
	return devUserId;
}
LoginMgr.prototype.storeDevUserId = function (devUserId) {
	btiao.clientPersist.set("btiao.devUserId", devUserId);
}
LoginMgr.prototype.retriveDevId = function () {
	return btiao.clientPersist.get("btiao.devUserId");
}
LoginMgr.prototype.genDevUserId = function () {
	var myDate = new Date();
	var devUserId = "007_" + myDate.getTime();
	return devUserId;
}
LoginMgr.prototype.usrLogin = function () {
	var friendUid = $("#inUserNameToLogin").val();
	var usrPasswd = $("#inUserPasswdToLogin").val();
	
	$.ajax({
		type: "PUT",
		url: userRoot+"/fusers/"+friendUid+logNodeName+"/0",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+friendUid+'",token:""}, \
			id:"'+friendUid+'", \
			passwd:"'+usrPasswd+'", \
			authType:0 \
		}',
		success: function(d) {
			if (d.errCode == 0) {
				this.logined = true;

				btiao.loginMgr.friendUid = friendUid;
				btiao.loginMgr.user = d.content.uId;
				btiao.loginMgr.token = d.content.token;
				
				btiao.clientPersist.set("btiao.devUserId", btiao.loginMgr.user);
				btiao.clientPersist.set("btiao.devPasswd", usrPasswd);
				
				btiao.firstPage.prepare();
			} else {
				btiao.util.tip("用户名或密码错误");
			}
		}
	});
}
LoginMgr.prototype.getPasswd = function() {
	return btiao.clientPersist.get("btiao.devPasswd");
}
LoginMgr.prototype.devLogin = function () {
	var loginUser = this.getDevUserId();
	var usrPasswd = this.getPasswd();
	var newDevice = false;
	if (usrPasswd == undefined) {
		newDevice = true;
		usrPasswd = loginUser;
	}
	
	$.ajax({
		type: "PUT",
		url: userRoot+"/users/"+loginUser+logNodeName+"/0",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:""}, \
			id:"'+loginUser+'", \
			passwd:'+usrPasswd+', \
			authType:0 \
		}',
		success: function(d) {
			if (d.errCode == 0) {
				this.logined = true;

				btiao.loginMgr.user = loginUser;
				btiao.loginMgr.token = d.content.token;
				
				btiao.clientPersist.set("btiao.devUserId", btiao.loginMgr.user);
				btiao.clientPersist.set("btiao.devPasswd", usrPasswd);
				
				btiao.firstPage.prepare();
			} else {
				btiao.util.tip("密码错误");
			}
		}
	});
}
LoginMgr.prototype.devLogout = function () {
	var loginUser = btiao.loginInfo.user;
	var token = btiao.loginInfo.token;
	
	$.ajax({
		type: "DELETE",
		url: userRoot+"/users/"+loginUser+logNodeName+"/" + token,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			uId:"'+loginUser+'", \
			token:"'+token+'" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
			if (d.errCode == 0) {
				btiao.loginInfo.token = "";
			}
		}
	})
}

function Log() {
	if (navigator.userAgent.match(/Android/i) ||
			navigator.userAgent.match(/iPhone/i)) {
		this.isMobile = true;
	} else {	
		this.isMobile = false;
	}
}
Log.prototype.l = function (str) {
	if (!!this.isMobile) {
		alert(str);
	} else {
		alert(str);
	}
}

btiao.reg("log", new Log());
btiao.reg("clientPersist", new ClientPersist());
btiao.reg("util", new Util());
btiao.reg("loginMgr", new LoginMgr());

var oldInit = window.onload;
window.onload = function() {
	if (!!oldInit) {
		(oldInit)();
	}
	
	$("#btiaoTip").popup();
	$("#btiaoOverTip").popup({overlayTheme: "a"});
	
	$("#labProductNum").slider({
		theme:"c",trackTheme:"c",highlight:true,mini:false
	});
	$("#labProductNum").change(function(){
		btiao.purchasePage.changePurchaseNum();
	});
	$("#labProductNum").slider("refresh");
	
	$("#actClearDeviceInfo").click(function(){
		btiao.clientPersist.clearAll();
	})

	$("#btDevLogin").click(function(){
		btiao.loginMgr.devLogin()
		return false;
	});
	$("#actUsrLogin").click(function(){
		btiao.loginMgr.usrLogin();
	});
	
	$("#actNewInfo").click(function(){
		btiao.updateInfoPage.prepare(true);
		return false;
	});
	$("#actMoreBlockInfos").click(function(){
		btiao.firstPage.moreBlockInfos();
	});
	$("#actDisplayValidInfo").click(function(){
		btiao.firstPage.actDisplayValidInfo();
	});
	$("#actDisplayHistoryInfo").click(function(){
		btiao.firstPage.actDisplayHistoryInfo();
	});
	
	$("#actListOrders").click(function(){
		btiao.orderListPage.prepare(btiao.firstPage.curPosInfo.id);
		return false;
	});
	$("#actMoreOrders").click(function(){
		btiao.orderListPage.moreOrders();
		return false;
	});
	
	$("#actPurchase").click(function(){
		btiao.purchasePage.purchase();
		return false;
	});
	
	$("#actEnterPurchase").click(function(){
		btiao.purchasePage.prepare(btiao.detailPage.info);
		return false;
	});
	
	$("#actDisplayTodoOrder").click(function(){
		btiao.orderListPage.actDisplayTodoOrder();
	})
	
	$("#actDisplayHistoryOrder").click(function(){
		btiao.orderListPage.actDisplayHistoryOrder();
	})
	
	$("#actUpdateInfo").click(function(){
		btiao.updateInfoPage.actUpdateInfo();
	});
	
	$("#actEnterUsrExtInfo").click(function(){
		btiao.usrExtInfoPage.prepare();
	});
	
	$("#actSetUsrInfo").click(function() {
		btiao.usrExtInfoPage.actSetUsrInfo();
	})
	
	$("#actToHomeArea").click(function(){
		var ext = btiao.usrExtInfoPage.usrExtInfo;
		if (!!ext) {
			if (!!ext.positionId && ext.positionId != "") {
				btiao.firstPage.enterPosition(ext.positionId);
			} else {
				btiao.util.tip("您还未设置此个人信息，从右上角入口进入!");
			}
		}
	})
	
	$("#actToUpArea").click(function(){
		var pid = btiao.firstPage.curPosInfo.pid;
		if (pid == undefined || pid == "") {
			btiao.util.tip("已经到最顶级了")
		}
		btiao.firstPage.enterPosition(pid);
	});
}

})();
