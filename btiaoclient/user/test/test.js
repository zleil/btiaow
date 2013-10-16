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
			uId:"zleil" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	})
}

var posRoot = "/btiao/product";
function putPos() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "PUT",
		url: posRoot+"/positions/__n",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "1", \
			name: "中国", \
			desc: "中国" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function getPos() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: posRoot+"/positions/1",
		contentType: "application/json; charset=UTF-8",
		data: {
			__opUsrInfo: {uId: loginUser, token: token}
		},
		success: function(d) {
			$("#idOut").append("result="+d.errCode+
					",content.id="+d.content.id+
					",content.pid="+d.content.pid+
					",content.name="+d.content.name+
					",content.desc="+d.content.desc
					);
		}
	})
}
function postPos() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "POST",
		url: posRoot+"/positions/1",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "1", \
			name: "中国 new", \
			desc: "中国 new" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function delPos() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "DELETE",
		url: posRoot+"/positions/1",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id:"1" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	})
}

function putInfo() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "PUT",
		url: posRoot+"/positions/1/infos/__n",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "1", \
			type: "sale", \
			posId: "1", \
			desc: "脉动买一送一" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function getInfo() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: posRoot+"/positions/1/infos/1",
		contentType: "application/json; charset=UTF-8",
		data: {
			__opUsrInfo: {uId: loginUser, token: token}
		},
		success: function(d) {
			$("#idOut").append("result="+d.errCode+
					",content.id="+d.content.id+
					",content.posId="+d.content.posId+
					",content.type="+d.content.type+
					",content.desc="+d.content.desc
					);
		}
	})
}
function postInfo() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "POST",
		url: posRoot+"/positions/1/infos/1",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "1", \
			posId: "2", \
			desc: "脉动七折" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function delInfo() {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "DELETE",
		url: posRoot+"/positions/1/infos/1",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id:"1" \
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
	
	$("#idPutPos").click(putPos);
	$("#idGetPos").click(getPos);
	$("#idPostPos").click(postPos);
	$("#idDelPos").click(delPos);
	
	$("#idPutInfo").click(putInfo);
	$("#idGetInfo").click(getInfo);
	$("#idPostInfo").click(postInfo);
	$("#idDelInfo").click(delInfo);
}

