var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

var loginUser = "";
var token = "";

function log(str) {
	$("#idOut").prepend('<p style="margin:0;padding:0;">'+str+'</p>');
}
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
				log("token="+token);
			}

			log("result="+d.errCode);
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
			log("result="+d.errCode);
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
			log("result="+d.errCode);
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
			log("result="+d.errCode+
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
			log("<br>result="+d.errCode);
			for (var o in d.content) {
				log("\n{id="+d.content[o].id+",nick="+d.content[o].nick+"},");
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
			log("result="+d.errCode);
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
			log("result="+d.errCode);
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
			desc: "'+desc+'", \
			price: 1 \
		}',
		success: function(d) {
			log("result="+d.errCode);
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
			log("result="+d.errCode+
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
			log("result="+d.errCode);
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
			log("result="+d.errCode);
		}
	})
}

function putInfo(posId, infoId, infoDesc, newPrice, oldPrice) {
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
			desc: "'+infoDesc+'", \
			oldPrice: "'+oldPrice+'", \
			price: "'+newPrice+'" \
		}',
		success: function(d) {
			log("result="+d.errCode);
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
			log("result="+d.errCode+
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
			desc: "'+infoDesc+'", \
			price: 20 \
		}',
		success: function(d) {
			log("result="+d.errCode);
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
			log("result="+d.errCode);
		}
	})
}
function getAllInfo(posId, lastId) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: posRoot+"/positions/"+posId+"/infos",
		contentType: "application/json; charset=UTF-8",
		data: {
			__opUsrInfo: {uId: loginUser, token: token},
			func: "normal",
			num: 10,
			lastId: (!!lastId ? lastId : "")
		},
		success: function(d) {
			if (d.errCode == 0) {
				for (var idx in d.content) {
					var info = d.content[idx];
					log("info.id="+info.id+
						",info.posId="+info.posId+
						",info.type="+info.type+
						",info.desc="+info.desc
						);
				}
			} else {
				log("result="+d.errCode);
			}
			
		}
	})
}

function putOrder(posId, orderId, productId, productNum, totalPrice, fromUser) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "PUT",
		url: posRoot+"/positions/"+posId+"/orders/__n",
		contentType: "application/json; charset=UTF-8",
		data: '{ \
			__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}, \
			orderId: "'+orderId+'", \
			posId: "'+posId+'", \
			productId: "'+productId+'", \
			productNum: "'+productNum+'", \
			totalPrice: "'+totalPrice+'", \
			fromUser: "'+fromUser+'" \
		}',
		success: function(d) {
			log("putOrder.result="+d.errCode);
		}
	});
}
function getAllOrder(posId, lastId) {
	var loginUser = $("#idLoginUser").attr("value");
	var token = $("#idToken").attr("value");
	$.ajax({
		type: "GET",
		url: posRoot+"/positions/"+posId+"/orders",
		contentType: "application/json; charset=UTF-8",
		data: {
			__opUsrInfo: {uId: loginUser, token: token},
			func: "normal",
			num: 10,
			lastId: (!!lastId ? lastId : "")
		},
		success: function(d) {
			if (d.errCode == 0) {
				for (var idx in d.content) {
					var order = d.content[idx];
					log("order.orderId="+order.orderId+
						",order.posId="+order.posId+
						",order.productId="+order.productId+
						",order.productNum="+order.productNum+
						",order.totalPrice="+order.totalPrice+
						",order.fromUser="+order.fromUser
						);
				}
			} else {
				log("result="+d.errCode);
			}
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
		postPos(1000000,"望春园","北苑最好的小区，环境非常优雅，还有个小区幼儿园，附近小区很多小孩都在这里上学");
		postPos(1000001,"6#0105","本店卖雪糕/汽水/零食，新鲜蔬菜，馒头等主食，纯净水等，送货上门！");
	});
	$("#idDelPos").click(function(){
		delPos(1000001);
		delPos(1000000);
	});
	
	$("#idPutInfo").click(function(){
		putInfo(1000001, 1, "脉动买一送一！", 450, 450);
		setTimeout(function(){putInfo(1000001, 2, "康师傅方便面促销，10桶20元！", 2000, 3500);},1000);
		setTimeout(function(){putInfo(1000001, 3, "可爱多，30元/12个！", 3000, 3600);},2000);
		setTimeout(function(){putInfo(1000001, 4, "西芹，最后一把，半价甩！", 500, 800);},3000);
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
		delInfo(1000001, 2);
		delInfo(1000001, 3);
		delInfo(1000001, 4);
	});
	$("#idGetAllInfo").click(function(){
		getAllInfo(1000001);
		getAllInfo(1000001, 2);
	});
	$("#idPutOrder").click(function(){
		putOrder(1000001,1,1,3,1350,"_mgr0");
		setTimeout(function(){putOrder(1000001,2,2,10,20000,"_mgr0")}, 1000);
	});
	$("#idGetAllOrder").click(function(){
		getAllOrder(1000001);
	})
}

