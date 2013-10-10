var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

var loginUser = "";
var token = "";

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
			if (d.errCode == 0) {
				token = d.content.token;
				$("#idToken").attr("value", token);
				$("#idOut").append("token="+token);
			}

			$("#idOut").append("result="+d.errCode);
		}
	});
}

function delLogout() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	
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
				token = "";
			}
		}
	})
}

function putUsr() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "PUT",
		url: userRoot+"/users/__n",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
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
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: userRoot+"/users/zleil",
		contentType: "application/json; charset=UTF-8",
		data: {
			__opUsrInfo: {uId: loginUser, token: token}
		},
		success: function(d) {
			$("#idOut").append("result="+d.errCode+
					"\ncontent.id="+d.content.id+
					"\ncontent.nick="+d.content.nick);
		}
	})
}
function getAllUsr() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: userRoot+"/users",
		contentType: "application/json; charset=UTF-8",
		data: {
			__opUsrInfo: {uId: loginUser, token: token}
		},
		success: function(d) {
			$("#idOut").append("<br>result="+d.errCode);
			for (var o in d.content) {
				$("#idOut").append("\n{id="+d.content[o].id+",nick="+d.content[o].nick+"},");
			}
		}
	})
}
function postUsr() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "POST",
		url: userRoot+"/users/zleil",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "zleil", \
			nick: "习远2, he is a good man!" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function delUsr() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "DELETE",
		url: userRoot+"/users/zleil",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			uId:"'+loginUser+'" \
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
	
	$("#idGetAll").click(getAllUsr);
}

