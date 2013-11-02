(function(){
var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

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
	if (localStorage) {
		localStorage["btiao.devUserId"] = devUserId;
	}
}
function retriveDevId() {
	if (localStorage) {
		return localStorage["btiao.devUserId"];
	} else {
		return undefined;
	}
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
				
				btiao.loginInfo = new Object();
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

var oldInit = window.onload;
window.onload = function() {
	$("#btDevLogin").click(devLogin);
	if (!!oldInit) {
		(oldInit)();
	}
	
	$("#actAddOne").click(btiao.purchaseAddOne);
	$("#actSubOne").click(btiao.purchaseSubOne);
	
	$("#actPurchase").click(btiao.purchase);
}

})();