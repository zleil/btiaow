var userRoot = "/btiao/usrmgr";
var logNodeName = "/auth";

var loginUser = "";
var token = "";

function log(str) {
	$("#idOut").prepend('<p style="margin:0;padding:0;">'+str+'</p>');
}
function getJsonObjStr(obj) {
	var r = "{";
	
	var first = true;
	for (attrName in obj) {
		if (!first) r += ",";
		if (obj[attrName] instanceof Object) {
			r += attrName + ":" + getJsonObjStr(obj[attrName]);
		} else {
			r += attrName + ":" + "\"" + obj[attrName] + "\"";
		}		
		first = false;
	}
	r += "}";
	
	return r;
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
			log(getJsonObjStr(d));
			
			if (d.errCode == 0) {
				token = d.content.token;
				$("#idToken").attr("value", token);
			}
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
			
			if (d.errCode == 0) {
				token = "";
			}
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			passwd: "zleil", \
			authType: 0 \
		}',
		success: function(d) {
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log("getAllUsr.result="+d.errCode);
			for (var o in d.content) {
				log(getJsonObjStr(d.content[o]));
			}
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			owner: "zleil" \
		}',
		success: function(d) {
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log(getJsonObjStr(d));
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			log("result="+d.errCode);
			if (d.errCode == 0) {
				for (var idx in d.content) {
					var info = d.content[idx];
					log(getJsonObjStr(info));
				}
			}
		},
		error : function (XMLHttpRequest, textStatus, errorThrown) {
			log("failed. textStatus="+textStatus);
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
			id: "'+orderId+'", \
			posId: "'+posId+'", \
			productId: "'+productId+'", \
			productNum: "'+productNum+'", \
			totalPrice: "'+totalPrice+'", \
			fromUser: "'+fromUser+'" \
		}',
		success: function(d) {
			log(getJsonObjStr(d));
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
					log(getJsonObjStr(order));
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
	
	$("#putSimplePositions").click(function(){
		//省、市、区、区域、小区、家、个人
		putPos(1000001,"6#0105","6号楼小卖部", 100000000);
		setTimeout(function(){
			putPos(1000000,"望春园","北苑最好的小区，环境优雅，还有个小区幼儿园！", 0);
		},1);
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
//		setTimeout(function(){putInfo(1000001, 2, "康师傅方便面促销，10桶20元！", 2000, 3500);},1000);
//		setTimeout(function(){putInfo(1000001, 3, "可爱多，30元/12个！", 3000, 3600);},2000);
//		setTimeout(function(){putInfo(1000001, 4, "西芹，最后一把，半价甩！", 500, 800);},3000);
		putInfo(1000001, 2, "康师傅方便面促销，10桶20元！", 2000, 3500);
		putInfo(1000001, 3, "可爱多，30元/12个！", 3000, 3600);
		putInfo(1000001, 4, "西芹，最后一把，半价甩！", 500, 800);
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
//		setTimeout(function(){delInfo(1000001, 2);},1000);
//		setTimeout(function(){delInfo(1000001, 3);},2000);
//		setTimeout(function(){delInfo(1000001, 4);},3000);
	});
	$("#idGetAllInfo").click(function(){
		getAllInfo(1000001);
		getAllInfo(1000001, 2);
	});
	$("#idPutOrder").click(function(){
		putOrder(1000001,1,1,3,1350,"_mgr0");
		setTimeout(function(){putOrder(1000001,2,2,10,20000,"_mgr0")}, 2000);
	});
	$("#idGetAllOrder").click(function(){
		getAllOrder(1000001);
	});
	$("#idGetId").click(function(){
		var loginUser = $("#idLoginUser").attr("value");
		var token = $("#idToken").attr("value");
		
		var idName = $("#idId").attr("value");
		$.ajax({
			type: "POST",
			url: posRoot+"/getId/"+idName,
			contentType: "application/json; charset=UTF-8",
			data: '{ \
				__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"} \
			}',
			success: function(d) {
				log(getJsonObjStr(d));
			}
		})
	});
	
	function getTestBlockElm(child) {
		while (child != null) {
			var clzs = $(child).attr("class");
			if (clzs.match(".*testBlock.*").length > 0){
				return child;
			}
			child = child.parentNode;
		}
		
		return null;
	}
	function constructJsonObj(testBlockElm) {
		var allInput = $("input",testBlockElm);
		var obj = "";
		for (var idx=0; idx<allInput.length; ++idx) {
			var input = $(allInput[idx]);
			var name = input.attr("id");
			var value = input.attr("value");
			obj += "," + name + ":" + "\"" + value + "\"";
		}
		return obj;
	}
	function constructUrl(urlTemplate, testBlockElm) {
		var allInput = $("input",testBlockElm);
		for (var idx=0; idx<allInput.length; ++idx) {
			var input = $(allInput[idx]);
			var name = input.attr("id");
			var value = input.val();
			urlTemplate = urlTemplate.replace("{"+name+"}", value);
		}
		
		return urlTemplate;
	}
	$(".comTestOp").click(function(e){
		var loginUser = $("#idLoginUser").attr("value");
		var token = $("#idToken").attr("value");
		
		var testBlockElm = getTestBlockElm(e.target.parentNode);
		var objStr = constructJsonObj(testBlockElm);
		var urlTemplate = $(e.target).attr("url");
		var url = constructUrl(urlTemplate, testBlockElm);
		var op = $(e.target).text();
		
		if (op == "PUT" || op == "POST") {
			$.ajax({
				type: op,
				url: "http://localhost"+url,
				contentType: "application/json; charset=UTF-8",
				data: '{ \
					__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"}' +
					objStr + ' \
				}',
				success: function(d) {
					log(getJsonObjStr(d));
				}
			})
		} else if (op == "GET") {
			$.ajax({
				type: op,
				url: "http://localhost"+url,
				contentType: "application/json; charset=UTF-8",
				data: {__opUsrInfo:{uId: loginUser, token: token}},
				success: function(d) {
					log(getJsonObjStr(d));
				}
			});
		} else if (op == "DELETE") {
			$.ajax({
				type: op,
				url: "http://localhost"+url,
				contentType: "application/json; charset=UTF-8",
				data: '{ \
					__opUsrInfo:{uId:"'+loginUser+'",token:"'+token+'"} \
				}',
				success: function(d) {
					log(getJsonObjStr(d));
				}
			})
		} else if (op == "GETALL") {
			$.ajax({
				type: "GET",
				url: "http://localhost"+url,
				contentType: "application/json; charset=UTF-8",
				data: {
					__opUsrInfo: {uId: loginUser, token: token},
					func: "normal",
					num: 10,
					lastId: ""
				},
				success: function(d) {
					log("result="+d.errCode);
					if (d.errCode == 0) {
						for (var idx in d.content) {
							var info = d.content[idx];
							log(getJsonObjStr(info));
						}
					}
				}
			});
		}
	});
}

