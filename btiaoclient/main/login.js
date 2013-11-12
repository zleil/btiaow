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
}
ClientPersist.prototype.test = 1;
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
			r += attrName + ":" + getJsonObjStr(obj[attrName]);
		} else {
			r += attrName + ":" + "\"" + obj[attrName] + "\"";
		}
	}
	return r;
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
			btiao.log.l("购买失败，请确认网络无故障，若无故障则可能是系统问题:"+textStatus);
		}
	});
}
Util.prototype.decoratePrice = function(price) {
	return (price*1.0)/100;
}

function LoginMgr() {
	this.logined = false;
	this.user = undefined;
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

LoginMgr.prototype.devLogin = function () {
	var loginUser = this.getDevUserId();
	var usrPasswd = loginUser;
	
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
				this.logined = false;

				btiao.loginMgr.user = loginUser;
				btiao.loginMgr.token = d.content.token;
				
				btiao.firstPage.prepare();
			} else {
				alert("login error!");
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
	if (navigator.userAgent.match(/Android/i)) {
		this.isAndroid = true;
	} else {	
		this.isAndroid = false;
	}
}
Log.prototype.l = function (str) {
	if (!!this.isAndroid) {
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
	
	$("#btDevLogin").click(function(){btiao.loginMgr.devLogin()});

	$("#labProductNum").slider({
		theme:"c",trackTheme:"c",highlight:true,mini:false
	});
	$("#labProductNum").change(btiao.changePurchaseNum);
	$("#labProductNum").slider("refresh");
	
	$("#actRefreshFirstPage").click(function(){
		btiao.firstPage.prepare();
	});
	$("#actListOrders").click(function(){
		btiao.firstPage.goToOrderListPage();
	})
}

})();
