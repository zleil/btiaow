(function(){
var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

/**
 * 封装持久化接口，确保所有持久化到客户端的变量，必须在此类中定义
 */
function ClientPersist() {
	this.devUserId = "devUserId";
}
ClientPersist.prototype.get = function(name) {
	if (!!!localStorage) return undefined;
	
	if (!eval("!!this."+name)) {
		throw Exception("get a unkown persist variable["+name+"]");
	}
	
	return localStorage[name];
}
ClientPersist.prototype.set = function(name, value) {
	if (!!!localStorage) return undefined;
	
	if (!eval("!!this."+name)) {
		throw Exception("set a unkown persist variable["+name+"]");
	}
	
	localStorage[name] = value;
}

function getDevUserId() {
	var devUserId = retriveDevId();
	if (!!devUserId) {
		return devUserId;
	}
	
	devUserId = genDevUserId();
	storeDevUserId(devUserId);
	return devUserId;
}
function storeDevUserId(devUserId) {
	btiao.clientPersist.set("btiao.devUserId", devUserId);
}
function retriveDevId() {
	return btiao.clientPersist.get("btiao.devUserId");
}
function genDevUserId() {
	var myDate = new Date();
	var devUserId = "007_" + myDate.getTime();
	return devUserId;
}

function devLogin() {
	var loginUser = getDevUserId();
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
				var token = d.content.token;
				
				btiao.loginInfo = {};
				btiao.loginInfo.user = loginUser;
				btiao.loginInfo.token = token;
				
				//TODO change areaId to user's default areaId
				var posId = 1000001;
				
				btiao.preparePgFirst(posId);
			} else {
				alert("login error!");
			}
		}
	});
}
function devLogout() {
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

btiao.log = function (str) {
	if (!!btiao.log.android) {
		alert(str);
	} else {
		alert(str);
	}
}

var oldInit = window.onload;
window.onload = function() {
	if (navigator.userAgent.match(/Android/i)) {
		btiao.log.android = true;
	} else {	
		btiao.log.android = false;
	}
	
	if (!!oldInit) {
		(oldInit)();
	}
	
	$("#btDevLogin").click(devLogin);

	$("#labProductNum").slider({
		theme:"c",trackTheme:"c",highlight:true,mini:false
	});
	$("#labProductNum").change(btiao.changePurchaseNum);
	$("#labProductNum").slider("refresh");
	
	btiao.reg("clientPersist", new ClientPersist());
}

})();
