(function(){
if (!window.onload) {
	window.__zleilinits = [];
	window.onload = function() {
		for (var initfunc in window.__zleilinits) {
			(window.__zleilinits[initfunc])(); 
		}
	};
}

var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

function genDevUserId() {
	var myDate = new Date();
	return "007_" + myDate.getTime();
}

function devLogin() {
	var loginUser = genDevUserId();
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

var init = function() {
	$("#btDevLogin").click(devLogin);
}
window.__zleilinits.push(init);
})();