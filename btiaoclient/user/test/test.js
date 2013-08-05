var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

var loginUser;
var token;

function putLogin() {
	loginUser = $("#idLoginUser").attr("value");
	var passwd = $("#idLoginPasswd").attr("value");
	
	$.ajax({
		type: "PUT",
		url: userRoot+"/users/"+loginUser+logNodeName+"/0",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id:"'+loginUser+'", \
			passwd:"zleil", \
			authType:0 \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
			var retToken = d.content.token;
			$("#idOut").append("token="+retToken);
		}
	});
}

function delLogout() {
	$.ajax({
		type: "DELETE",
		url: userRoot+"/users/zleil"+logNodeName+"/" + token,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:'+loginUser+',token:'+token+'} \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	})
}

function putUsr() {
	$.ajax({
		type: "PUT",
		url: userRoot+"/users/zleil",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:'+loginUser+',token:'+token+'}, \
			id: "zleil", \
			nick: "习远, he is a good man!", \
			passwd: "mypasswd", \
			authType: 0 \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function getUsr() {
	$.ajax({
		type: "GET",
		url: userRoot+"/users/zleil",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:'+loginUser+',token:'+token+'}, \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode+
					"\ncontent.id="+d.content.id+
					"\ncontent.nick="+d.content.nick);
		}
	})
}
function postUsr() {
	$.ajax({
		type: "POST",
		url: userRoot+"/users/zleil",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:'+loginUser+',token:'+token+'}, \
			id: "zleil", \
			nick: "习远2, he is a good man!" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function delUsr() {
	$.ajax({
		type: "DELETE",
		url: userRoot+"/users/zleil",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:'+loginUser+',token:'+token+'} \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	})
}

function onload() {
	$("#idPut").click(putUsr);
	$("#idGet").click(getUsr);
	$("#idPost").click(postUsr);
	$("#idDel").click(delUsr);
	
	$("#idLogin").click(putLogin);
	$("#idLogout").click(delLogout);
}

