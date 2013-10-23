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
function putPos(posId, name, desc, pId) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "PUT",
		url: posRoot+"/positions/__n",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "'+posId+'", \
			pid: "'+pId+'", \
			name: "'+name+'", \
			desc: "'+desc+'" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function getPos(posId) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: posRoot+"/positions/"+posId,
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
function postPos(posId,name,desc) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "POST",
		url: posRoot+"/positions/"+posId,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "'+posId+'", \
			name: "'+name+'", \
			desc: "'+desc+'" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function delPos(posId) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "DELETE",
		url: posRoot+"/positions/"+posId,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id:"'+posId+'" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	})
}

function putInfo(posId, infoId, infoDesc) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "PUT",
		url: posRoot+"/positions/"+posId+"/infos/__n",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "'+infoId+'", \
			type: "sale", \
			posId: "'+posId+'", \
			desc: "'+infoDesc+'" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function getInfo(posId, infoId) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: posRoot+"/positions/"+posId+"/infos/"+infoId,
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
function postInfo(posId, infoId, infoDesc) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "POST",
		url: posRoot+"/positions/"+posId+"/infos/"+infoId,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id: "'+infoId+'", \
			desc: "'+infoDesc+'" \
		}',
		success: function(d) {
			$("#idOut").append("result="+d.errCode);
		}
	});
}
function delInfo(posId, infoId) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "DELETE",
		url: posRoot+"/positions/"+posId+"/infos/"+infoId,
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			id:"'+infoId+'" \
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
	
	$("#idPutPos").click(function(){
		//省、市、区、区域、小区、家、个人
		putPos(1000001,"6#0105","6号楼小卖部", 100000000);
		putPos(1000000,"望春园","北苑最好的小区，环境优雅，还有个小区幼儿园！", 0);
	});
	$("#idGetPos").click(function(){
		getPos(1000001);
		getPos(1000000);
	});
	$("#idPostPos").click(function(){
		postPos(1000001,"望春园","北苑最好的小区，环境非常优雅，还有个小区幼儿园，附近小区很多小孩都在这里上学");
		postPos(1000000,"6#0105","6号楼小卖部，便宜！");
	});
	$("#idDelPos").click(function(){
		delPos(1000001);
		delPos(1000000);
	});
	
	$("#idPutInfo").click(function(){
		putInfo(1000001, 1, "脉动买一送一！");
		putInfo(1000001, 2, "康师傅方便面促销，10桶20元！");
		putInfo(1000001, 3, "可爱多，30元/12个！");
		putInfo(1000001, 4, "西芹，最后一把，半价甩！");
	});
	$("#idGetInfo").click(function(){
		getInfo(1000001, 1);
		getInfo(1000001, 2);
		getInfo(1000001, 3);
		getInfo(1000001, 4);
	});
	$("#idPostInfo").click(function(){
		postInfo(1000001, 1, "脉动买一送一，5折甩，截至时间今晚22：00点！");
		//postInfo(1000001, 4, "西芹，最后一把，半价甩，截至时间今晚22：00点！");
	});
	$("#idDelInfo").click(function(){
		delInfo(1000001, 1);
		//delInfo(1000001, 4);
	});
}

