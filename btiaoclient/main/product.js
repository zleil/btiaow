(function(){
//global variable declare
var productRoot = "/btiao/product";
var numPer = 10;
	
//function definition and init-code
function FirstPage() {
	this.curPosInfo = undefined;
	this.infos = {};
	this.lastId = "";
	this.displayValid = true; //false: display history info.
}
FirstPage.prototype.moreBlockInfos = function(){
	this.fillBlockInfo(this.lastId, numPer);
}
FirstPage.prototype.prepare = function() {
	var usrInfoExturl = productRoot + "/usrInfoExt/" + btiao.loginMgr.user;
	btiao.util.getObj(usrInfoExturl, function(d){
		var curPosId = undefined;
		if (d.errCode == 0) {
			btiao.usrExtInfoPage.usrExtInfo = d.content;
			curPosId = btiao.usrExtInfoPage.usrExtInfo.positionId;
		}
		
		var defaultToPos = "1000001";
		if (curPosId == undefined) curPosId = defaultToPos;
		
		btiao.firstPage.enterPosition(curPosId, defaultToPos);
	});
}
//if enter curPosId failed, try to enter defaultToPos if you give this argument.
FirstPage.prototype.enterPosition = function(curPosId, defaultToPos){
	var posUrl = productRoot+"/positions/"+curPosId;
	btiao.util.getObj(posUrl, function(d){
		if (d.errCode != 0) {
			if (defaultToPos == undefined){
				btiao.util.tip("您访问的位置不存在了，更改下个人设置吧");
				return;
			} else {
				//此时应该是”回家“的动作
				btiao.util.tipOver("您设置的默认位置不存在了，建议进入默认位置后修改个人设置", 0,
					function(){
						btiao.firstPage.enterPosition(defaultToPos);
					}
				);
				
				return;
			}
		} else {
			btiao.firstPage.curPosInfo = d.content;
			btiao.util.changeTitle(btiao.firstPage.curPosInfo.name);
		}
		
		var h3 = $(".posName");
		h3.empty();
		h3.append(btiao.firstPage.curPosInfo.name);
		
		var posOwnerId = btiao.firstPage.curPosInfo.ownerUser;
		var usrInfoExturl = productRoot + "/usrInfoExt/" + posOwnerId;
		btiao.util.getObj(usrInfoExturl, function(d){
			if (d.errCode == 0) {
				$("#posOwner").text(d.content.friendUid);
			}
		});
		
		btiao.firstPage.fillBlockInfo("", numPer);
	});
}
FirstPage.prototype.actDisplayValidInfo = function(){
	this.displayValid = true;
	$("#actDisplayValidInfo").prop("checked",true).checkboxradio("refresh");
	$("#actDisplayHistoryInfo").prop("checked",false).checkboxradio("refresh");
	
	this.fillBlockInfo("", numPer);
}
FirstPage.prototype.actDisplayHistoryInfo = function(){
	this.displayValid = false;
	$("#actDisplayValidInfo").prop("checked",false).checkboxradio("refresh");
	$("#actDisplayHistoryInfo").prop("checked",true).checkboxradio("refresh");
	
	this.fillBlockInfo("", numPer);
}
FirstPage.prototype.preDecorateUI = function() {
//	if (this.curPosInfo.ownerUser != btiao.loginMgr.user) {
//		this.displayValid = true;
//		$("#cbBlockInfoQueryType").css("display", "none");
//		$("#actNewInfo:parent").css("display", "none");
//	} else {
//		$("#cbBlockInfoQueryType").css("display", "block");
//	}
}
FirstPage.prototype.fillBlockInfo = function (lastInfoId, num) {
	var url = productRoot+"/positions/"+this.curPosInfo.id+(this.displayValid?"/infos":"/historyInfos");
	btiao.util.getAllObj(url, function(d){
		if (d.errCode == 0) {
			if (lastInfoId == "") {
				this.infos = {};
				btiao.util.clearListViewData("#lstBlockInfo");
			}
			
			for (var idx in d.content) {
				btiao.firstPage.fillAInfo(d.content[idx]);
			}
			
			if (d.content.length < num) {
				$("#actMoreBlockInfos").addClass("ui-disabled");
			} else {
				$("#actMoreBlockInfos").removeClass("ui-disabled");
			}
			
			$.mobile.changePage($("#pgFirst"));
			btiao.firstPage.preDecorateUI();
			$("#lstBlockInfo").listview("refresh");
		} else {
			btiao.util.tip("获取信息失败", d.errCode);
		}
	}, num, lastInfoId);
}
FirstPage.prototype.onclickBlockInfo = function(elm) {
	var infoElm = btiao.util.getSpecificUpperElm(elm, "data-infoid");
	var infoId = $(infoElm).attr("data-infoid");
	btiao.detailPage.prepare(infoId);
	return false;
}
FirstPage.prototype.fillAInfo = function (info) {
	var html = "";
	html += '<li data-theme="e" data-infoid="'+info.id+'">';
	html += '<a onclick="btiao.firstPage.onclickBlockInfo(this);" data-transition="slide">';
	html += '<h3>'+info.desc+'</h3>';
	html += '<p>现价：';
	html += '<span'+((info.price < info.oldPrice) ? ' class="newLowPrice">':'>')+btiao.util.decoratePrice(info.price)+'</span>';
	html += (info.price < info.oldPrice)? ('，<span class="oldHighPrice">原价：'+btiao.util.decoratePrice(info.oldPrice)+'</span>') : '';
	html += '</p>';
	html += '</a>';
	html += info.state==0 ? this.getHistoryActHTML() : "";
	html += '</li>';
	
	$("#lstBlockInfo").append(html);
	this.infos[info.id] = info;
	this.lastId = info.id;
}
FirstPage.prototype.getHistoryActHTML = function() {
	return '<a title="点击下架产品" onclick="btiao.firstPage.historyBlockInfo(this);"></a>';
}
FirstPage.prototype.historyBlockInfo = function(elm) {
	var infoElm = btiao.util.getSpecificUpperElm(elm, "data-infoid");
	var infoId = $(infoElm).attr("data-infoid");
	
	var url = productRoot+"/positions/"+btiao.firstPage.curPosInfo.id+"/infos/"+infoId;
	var obj = {
			id: infoId,
			state:"1" //history
	};
	
	var callback = function(d) {
		$(callback.infoElm).remove();
		
		$("#lstBlockInfo").listview("refresh");
		btiao.firstPage.infos[callback.infoId].state = 1;
	};
	callback.infoElm = infoElm;
	callback.infoId = infoId;
	btiao.util.postObj(url, obj, callback);
	
	return false;
}

function DetailPage() {
	this.info = null;
}
DetailPage.prototype.prepare = function (infoId) {
	this.info = btiao.firstPage.infos[infoId];

	$("#idInfoId").text(this.info.id);
	$("#idInfoDesc").text(this.info.desc);
	$("#idInfoPrice").text(btiao.util.decoratePrice(this.info.price));
	$("#idInfoOldPrice").text(btiao.util.decoratePrice(this.info.oldPrice));
	if (this.info.state !=0) {
		$("#idInfoState").text("下架");
		$("#idInfoState").removeAttr("class");
		$("#idInfoState").addClass("historyInfoState");
	} else {
		$("#idInfoState").text("有效");
		$("#idInfoState").removeAttr("class");
		$("#idInfoState").addClass("validInfoState");
	}
	$("#idCreateTime").text(btiao.util.decorateTime(this.info.createTime));
	
	if (this.info.state != 0) {
		$("#actEnterPurchase").addClass("ui-disabled");
	} else {
		$("#actEnterPurchase").removeClass("ui-disabled");
	}
	
	$.mobile.changePage($("#pgDetail"));
}

function PurchasePage () {
	this.info = null;
	this.hasSetPurchaseNum = false;
}
PurchasePage.prototype.prepare = function(info) {
	this.info = info;
	
	$.mobile.changePage($("#pgPurchase"));
	if (!this.hasSetPurchaseNum) {
		$("#labProductNum").change(function(){
			btiao.purchasePage.changePurchaseNum();
		});
		this.hasSetPurchaseNum = true;
	}
	
	$('#labProductNum').val(1);
	$('#labProductNum').slider("refresh");
	this.changePurchaseNum();
	
	var lastDst = btiao.clientPersist.get("btiao.orderDst");
	var usrExtInfo = btiao.usrExtInfoPage.usrExtInfo;
	if (lastDst == undefined && 
		undefined != usrExtInfo && usrExtInfo.locationOfPos != ""){
		$("#orderDst").val(usrExtInfo.locationOfPos);
	}
	if (lastDst != undefined) {
		try {
			if (!isNaN(parseInt(lastDst))) {
				$("#orderDst").val(lastDst);
			}
		} catch (e) {}
	}
}
PurchasePage.prototype.changePurchaseNum = function() {
	if (this.info == null) return;
	
	var num = parseInt($('#labProductNum').val());
	var price = this.info.price;
	var truePrice = btiao.util.decoratePrice(price*num);
	$('#labTotalPrice').text(truePrice);
}
PurchasePage.prototype.checkOrderDst = function () {
	return !!$("#orderDst").val().match(/[0-9]{8}/);
}
PurchasePage.prototype.dispPurchaseAlert = function () {
	$("#orderDstNote").addClass("alertArea");
	btiao.util.tip("输入有误哦~");
}
PurchasePage.prototype.undispPurchaseAlert = function () {
	$("#orderDstNote").removeClass("alertArea");
}
PurchasePage.prototype.setLastOrderDst = function () {
	var value = $("#orderDst").val();
	btiao.clientPersist.set("btiao.orderDst", value);
}
PurchasePage.prototype.purchase = function() {
	if (!this.checkOrderDst()) {
		this.dispPurchaseAlert();
		return;
	}
	
	btiao.util.getId("orderId", function(orderId) {
		var productNum = parseInt($('#labProductNum').val());
		var url = productRoot+"/positions/"+btiao.firstPage.curPosInfo.id+"/orders/__n";
		
		var orderDst = $("#orderDst").val();
		var mark = $("#idProductMark").val();
		var obj = {
				id: orderId,
				posId: btiao.firstPage.curPosInfo.id,
				productId: btiao.detailPage.info.id,
				productDesc: btiao.detailPage.info.desc,
				productNum: productNum,
				totalPrice: btiao.detailPage.info.price*productNum,
				fromUser: btiao.loginMgr.user,
				orderDst: orderDst,
				mark: mark
		}
		btiao.util.putObj(url, obj, function(d) {
			if (d.errCode != 0) {
				btiao.util.tip("添加订单出错", d.errCode);
			} else {
				btiao.purchasePage.undispPurchaseAlert();
				btiao.purchasePage.setLastOrderDst();
				
				$.mobile.changePage($("#pgFirst"));
			}
		});
	});
}

function OrderListPage() {
	this.orders = {};
	this.posId = "";
	this.lastOrderId = "";
	this.dispStyleTodo = "todo";
	this.dispStyleHistory = "history";
	this.displayStyle = this.dispStyleTodo;
}
OrderListPage.prototype.clearView = function() {
	btiao.util.clearListViewData("#lstOrderInfo");
	$("#lstOrderInfo").listview("refresh");
}
OrderListPage.prototype.prepare = function(posId) {
	this.posId = posId;
	this.fillAllOrder("", numPer);
}
OrderListPage.prototype.actDisplayTodoOrder = function() {
	this.displayStyle = this.dispStyleTodo;
	$("#actDisplayTodoOrder").prop("checked", true).checkboxradio("refresh");
	$("#actDisplayHistoryOrder").prop("checked", false).checkboxradio("refresh");
	this.clearView();
	
	this.prepare(this.posId);
}
OrderListPage.prototype.actDisplayHistoryOrder = function() {
	this.displayStyle = this.dispStyleHistory;
	$("#actDisplayTodoOrder").prop("checked", false).checkboxradio("refresh");
	$("#actDisplayHistoryOrder").prop("checked", true).checkboxradio("refresh");
	this.clearView();
	
	this.prepare(this.posId);
}
OrderListPage.prototype.moreOrders = function() {
	this.fillAllOrder(this.lastOrderId, numPer);
}
OrderListPage.prototype.fillAllOrder = function (lastOrderId, num) {
	var url = "";
	if (btiao.loginMgr.user == btiao.firstPage.curPosInfo.ownerUser) {
		$("#cbOrderFilter").css("display", "block");
		url = productRoot+"/positions/"+this.posId+(this.displayStyle==this.dispStyleTodo ? "/orders" : "/historyOrders");
	} else {
		$("#cbOrderFilter").css("display", "none");
		url = productRoot+"/users/"+btiao.loginMgr.user+(this.displayStyle==this.dispStyleTodo ? "/usrOrders" : "/usrHistoryOrders");
	}
	btiao.util.getAllObj(url, function(d){
		if (d.errCode == 0) {
			if (lastOrderId == "") {
				this.orders = {};
				btiao.util.clearListViewData("#lstOrderInfo");
			}
			
			if (lastOrderId == "" && d.content.length == 0 && btiao.orderListPage.displayStyle == this.dispStyleTodo) {
				$("#pgOrderListPage .noOrderTip").css("display", "block");
			} else {
				$("#pgOrderListPage .noOrderTip").css("display", "none");
			}
			
			if (d.content.length < num) {
				$("#actMoreOrders").addClass("ui-disabled");
			} else {
				$("#actMoreOrders").removeClass("ui-disabled");
			}
			
			for (var idx in d.content) {
				btiao.orderListPage.fillAOrder(d.content[idx]);
			}
			
			$.mobile.changePage($("#pgOrderListPage"));
			$("#lstOrderInfo").listview("refresh");
		} else {
			btiao.util.tip("获取订单失败", d.errCode);
		}
	}, num, lastOrderId);
}
OrderListPage.prototype.onclickOrderInfo = function(elm) {
	var orderId = undefined;
	var i=5;
	var orderElm = btiao.util.getSpecificUpperElm(elm, "data-orderid");
	var orderId = $(orderElm).attr("data-orderid");
	var orderInfo = btiao.orderListPage.orders[orderId];
	
	btiao.orderDetailPage.prepare(orderInfo);
	return false;
}
OrderListPage.prototype.getOrderStateClass = function(state) {
	return "orderState" + state;
}
OrderListPage.prototype.fillAOrder = function(orderInfo) {
	var finishActHtml = (orderInfo.state == 0) ? this.getFinishActHTML() : '';
	var totalPrice = btiao.util.decoratePrice(orderInfo.totalPrice);
	var markTxt = ((!!orderInfo.mark)&&orderInfo.mark!='')?('<p class="highLight">备注：'+orderInfo.mark+'</p>'):'';
	$("#lstOrderInfo").append(
			'<li data-theme="e" data-orderid="'+orderInfo.id+'">' +
			'<a onclick="btiao.orderListPage.onclickOrderInfo(this);" class="orderInfo data-transition="slide">' +
			'<h3>' + orderInfo.productDesc + '</h3>' +
			'<p><span class="state '+this.getOrderStateClass(orderInfo.state)+'">'+this.getStateDesc(orderInfo.state)+'</span>，订购时间： ' + btiao.util.decorateTime(orderInfo.createTime) + '</p>' +
			'<p>总价： ' + totalPrice + '元，数量：' + orderInfo.productNum + '，送货位置：' + orderInfo.orderDst + '</p>' +
			markTxt +
			'</a>'+
			finishActHtml +
			'</li>'
	);
	this.orders[orderInfo.id] = orderInfo;
	this.lastOrderId = orderInfo.id;
}
OrderListPage.prototype.getFinishActHTML = function() {
	return '<a title="点击完成交易" onclick="btiao.orderListPage.finishOrder(this);"></a>';
}
OrderListPage.prototype.finishOrder = function(elm) {
	var orderElm = btiao.util.getSpecificUpperElm(elm, "data-orderid");
	var orderId = $(orderElm).attr("data-orderid");
	var url = productRoot+"/positions/"+btiao.firstPage.curPosInfo.id+"/orders/"+orderId;
	var obj = {
			id: orderId,
			state:"2"
	};
	
	callback.elm = elm;
	callback.orderId = orderId;
	btiao.util.postObj(url, obj, function(d) {
		if (d.errCode != 0) {
			btiao.tip("处理失败");
			return;
		}
		
		var stateE = $(".state",callback.elm.parentNode);
		stateE.removeClass(btiao.orderListPage.getOrderStateClass(0));
		stateE.addClass(btiao.orderListPage.getOrderStateClass(2));
		
		$(callback.elm).remove();
		stateE.text(btiao.orderListPage.getStateDesc(2));
		
		$("#lstOrderInfo").listview("refresh");
		btiao.orderListPage.orders[callback.orderId].state = 2;
	});
}
OrderListPage.prototype.getStateDesc = function(state) {
	if (state == 0) {
		return '待送';
	} else if (state == 1) {
		return '取消';
	} else if (state == 2) {
		return '已付款';
	} else {
		return '...';
	}
}

function OrderDetailPage() {
	this.orderInfo = null;
}
OrderDetailPage.prototype.prepare = function (orderInfo) {
	this.orderInfo = orderInfo;
	
	var stateDesc = btiao.orderListPage.getStateDesc(this.orderInfo.state);
	var createTimeDesc = btiao.util.decorateTime(this.orderInfo.createTime);
	
	$("#labOrderId").text(this.orderInfo.id);
	$("#labState").text(stateDesc); 
	$("#labState").attr("class","");
	$("#labState").addClass(btiao.orderListPage.getOrderStateClass(this.orderInfo.state));
	$("#labDst").text(this.orderInfo.orderDst);
	$("#labOrderfromUser").text(this.orderInfo.fromUser);
	$("#labOrderDesc").text(this.orderInfo.productDesc);
	$("#labOrderPrice").text(btiao.util.decoratePrice(this.orderInfo.totalPrice));
	$("#labOrderProductNum").text(this.orderInfo.productNum);
	$("#labCreateTime").text(createTimeDesc);
	
	$.mobile.changePage($("#pgOrderDetailPage"));
}

function UpdateInfoPage(){
	this.isCreate = undefined;
	this.info = undefined;
}
UpdateInfoPage.prototype.prepare = function(isCreate, info) {
	this.isCreate = isCreate;
	if (!this.isCreate) {
		this.info = info;
		$("#inUpdateInfoDesc").text(this.info.desc);
		$("#inUpdateInfoPrice").text(this.info.price);
		$("#inUpdateInfoOldPrice").text(this.info.oldPrice);
	}
	$.mobile.changePage("#pgUpdateInfo");
}
UpdateInfoPage.prototype.checkValid = function(desc, price, oldPrice) {
	var r = true;
	if (!btiao.util.checkPriceValid(price)) {
		$("#for_inUpdateInfoPrice").addClass("alertArea");
		r = false;
	} else {
		$("#for_inUpdateInfoPrice").removeClass("alertArea");
	}
	if (!btiao.util.checkPriceValid(oldPrice)) {
		r = false;
		$("#for_inUpdateInfoOldPrice").addClass("alertArea");
	} else {
		$("#for_inUpdateInfoOldPrice").removeClass("alertArea");
	}
	if (desc == 0) {
		r = false;
		$("#for_inUpdateInfoDesc").addClass("alertArea");
	} else {
		$("#for_inUpdateInfoDesc").removeClass("alertArea");
	}
	return r;
}
UpdateInfoPage.prototype.actUpdateInfo = function() {
	var desc = $("#inUpdateInfoDesc").val();
	var price = $("#inUpdateInfoPrice").val();
	var oldPrice = $("#inUpdateInfoOldPrice").val();
	
	if (!btiao.updateInfoPage.checkValid(desc, price, oldPrice)) {
		btiao.util.tip("输入有误哦~");
		return;
	}
	
	btiao.util.getId("infoId", function(infoId) {
		price = btiao.util.formIntPrice(price);
		oldPrice = btiao.util.formIntPrice(oldPrice);
		
		var url = productRoot + "/positions/"+btiao.firstPage.curPosInfo.id+"/infos/" + infoId;
		var obj = {
				id: infoId,
				type: "product",
				posId: btiao.firstPage.curPosInfo.id,
				desc: desc,
				price: price,
				oldPrice: oldPrice
		}
		btiao.util.putObj(url, obj, function(d) {
			//$.mobile.changePage($("#pgFirst"));
			if (d.errCode != 0) {
				btiao.util.tip("添加信息错误", d.errCode);
			} else {
				btiao.firstPage.actDisplayValidInfo();
			}
		});
	});
}

function UsrExtInfoPage() {
	this.usrExtInfo = undefined;
}
UsrExtInfoPage.prototype.prepare = function() {
	if (!this.usrExtInfo) {
		var usrInfoExturl = productRoot + "/usrInfoExt/" + btiao.loginMgr.user;
		btiao.util.getObj(usrInfoExturl, function(d){
			var ext = btiao.usrExtInfoPage.usrExtInfo;
			
			if (d.errCode == 0) {
				ext = btiao.usrExtInfoPage.usrExtInfo = d.content;
			} else {
				//OBJ_NOT_IN_INFO_MODEL = 101
				if (d.errCode != 101) {
					btiao.util.tip("获取个人信息失败", d.errCode);
				} else {
					$("#inSetCurPosDefault").prop("checked", true);
					//$("#checkbox_inSetCurPosDefault").checkboxradio("refresh");
				}
			}
			
			$("#for_inSetCurPosDefault").text(btiao.firstPage.curPosInfo.name);
			
			$.mobile.changePage("#pgUsrExtInfo");
			btiao.usrExtInfoPage.setUsrInfoToUi(ext);
		});
	} else {
		$.mobile.changePage("#pgUsrExtInfo");
		$("#inSetCurPosDefault").prop("checked", false);
		$("#for_inSetCurPosDefault").text(btiao.firstPage.curPosInfo.name);
		btiao.usrExtInfoPage.setUsrInfoToUi(btiao.usrExtInfoPage.usrExtInfo);
	}
}
UsrExtInfoPage.prototype.setUsrInfoToUi = function (ext) {
	$("#for_inUserPasswdInfo").removeClass("alertArea");
	$("#for_inUserPasswdInfo2").removeClass("alertArea");
	$("#for_inUsrExtInfoFuid").removeClass("alertArea");
	$("#for_inUsrExtInfoLocationOfLivePos").removeClass("alertArea");
	
	if (!ext) return;
	
	if (!!ext.friendUid && ext.friendUid != "") {
		$("#inUsrExtInfoFuid").val(ext.friendUid);
		$("#inUsrExtInfoFuid").textinput("option", "disabled", true);
	}
	
	$("#inUsrExtInfoPos").val(ext.positionId);
	$("#inUsrExtInfoLivePos").val(ext.posToLive);
	$("#inUsrExtInfoLocationOfLivePos").val(ext.locationOfPos);
}
//get obj from ui, if any data is invalid, then give tips and return null
UsrExtInfoPage.prototype.getUsrInfoFromUi = function () {
	var valid = true;
	var obj = {};
	var friendUid = $("#inUsrExtInfoFuid").val();
	if (friendUid == "" || friendUid.length > 20 ||
		!!friendUid.match(/^[0-9|_]/)) {
		$("#for_inUsrExtInfoFuid").addClass("alertArea");
		valid = false;
	} else {
		$("#for_inUsrExtInfoFuid").removeClass("alertArea");
		obj.friendUid = friendUid;
	}
	
	var passwd = $("#inUserPasswdInfo").val();
	var passwd2 = $("#inUserPasswdInfo2").val();
	if (passwd != "" || passwd2 != "") {
		if (passwd != passwd2) {
			valid =false;
			$("#for_inUserPasswdInfo").addClass("alertArea");
			$("#for_inUserPasswdInfo2").addClass("alertArea");
		} else {
			obj.passwd = passwd;
			$("#for_inUserPasswdInfo").removeClass("alertArea");
			$("#for_inUserPasswdInfo2").removeClass("alertArea");
		}
	} else {
		obj.passwd = obj.friendUid;
	}
	
	var positionId = $("#inUsrExtInfoPos").val();
	if (positionId != "") {
		obj.positionId = positionId;
	}
	
	var posToLive = $("#inUsrExtInfoLivePos").val();
	if (posToLive != "") {
		obj.posToLive = posToLive;
	}
	
	var locationOfPos = $("#inUsrExtInfoLocationOfLivePos").val();
	if (locationOfPos != "" && !locationOfPos.match(/^[0-9]{8,8}$/)) {
		$("#for_inUsrExtInfoLocationOfLivePos").addClass("alertArea");
		valid = false;
	} else {
		$("#for_inUsrExtInfoLocationOfLivePos").removeClass("alertArea");
		obj.locationOfPos = locationOfPos;
	}
	
	return valid ? obj : null;
}
UsrExtInfoPage.prototype.actSetUsrInfo = function () {
	var usrInfoExturl = productRoot + "/usrInfoExt/" + btiao.loginMgr.user;
	var obj = this.getUsrInfoFromUi();
	if (obj == null) {
		return;
	}
	
	var isPut = true;
	if (!!this.usrExtInfo) {
		isPut = false;
	}
	
	btiao.util.putOrPosObj(isPut, usrInfoExturl, obj, function(d){
		if (d.errCode == 0) {
			btiao.usrExtInfoPage.usrExtInfo = obj;
			if (!!obj.passwd && obj.passwd != "") {
				btiao.clientPersist.set("btiao.devPasswd", obj.passwd);
			}
			
			$.mobile.changePage("#pgFirst");
		} else {
			btiao.util.tip("用户名重复哦", d.errCode);
		}
	});
}

function SubPosPage() {
	this.lastId = "";
	this.posId = undefined;
	this.parentId = undefined;
}
SubPosPage.prototype.prepare = function(posId) {
	if (posId == "1000001") {
		$("#actListUpperPos").addClass("ui-disabled");
	} else {
		$("#actListUpperPos").removeClass("ui-disabled");
	}
	
	this.posId = posId;
	this.fillAllSubPos("", numPer);
	
	var posUrl = productRoot+"/positions/"+this.posId;
	btiao.util.getObj(posUrl, function(d){
		if (d.errCode != 0) {
			btiao.tip("返回查询上级位置失败", d.errCode);
			return;
		}
		
		$("#labCurPosNameInSubList").text(d.content.name);
		btiao.subPosPage.parentId = d.content.pid;
	});
}
SubPosPage.prototype.prepareParent = function(posId) {
	if (!(this.parentId) || this.parentId == "1000001") {
		return;
	}
	
	btiao.subPosPage.prepare(this.parentId);
}
SubPosPage.prototype.moreSub = function() {
	fillAllSubPos(this.lastId, numPer*2);
}
SubPosPage.prototype.fillAllSubPos = function(lastId, num) {
	var url = productRoot+"/positions/"+this.posId+"/subPositions";
	btiao.util.getAllObj(url, function(d){
		if (d.errCode == 0) {
			if (d.content.length == 0) {
				btiao.util.tip("没有子位置");
				
				return;
			}
			
			if (lastId == "") {
				btiao.util.clearListViewData("#subPositions");
			}
			
			for (var idx in d.content) {
				var pos = d.content[idx];
				$("#subPositions").append(
						'<li data-theme="e" data-posid="'+pos+'">' +
						'<a onclick="btiao.firstPage.enterPosition('+pos.id+');" data-transition="slide">'+pos.name+'</a>' +
						'<a onclick="btiao.subPosPage.prepare('+pos.id+');">down</a>' +
						'</li>'
				);
				this.lastId = pos.id;
			}
			if (d.content.length < num) {
				$("#actMoreSubPos").addClass("ui-disabled");
			} else {
				$("#actMoreSubPos").removeClass("ui-disabled");
			}
			
			$.mobile.changePage($("#pgSubPos"));
			$("#subPositions").listview("refresh");
		} else {
			btiao.util.tip("获取子位置失败", d.errCode);
		}
	}, num, lastId);
}
function ChgPosOwnerPage () {}
ChgPosOwnerPage.prototype.prepare = function (){
	$("#for_inNewPosOwner").removeClass("alertArea");
	$.mobile.changePage("#pgChangePosOwner");
}
ChgPosOwnerPage.prototype.actChangePosOwner = function () {
	var newOwnerId = $("#inNewPosOwner").val();
	if (newOwnerId == "" || newOwnerId.match(/^[_|0-9].*$/)) {
		$("#for_inNewPosOwner").addClass("alertArea");
		btiao.util.tip("无效账号");
		return;
	} else {
		$("#for_inNewPosOwner").removeClass("alertArea");
	}
	
	var posId = btiao.firstPage.curPosInfo.id;
	var url = productRoot + "/positions/" + posId;
	btiao.util.postObj(url, 
		{uid:posId, ownerUser:newOwnerId}, 
		function(d){
			if (d.errCode != 0) {
				btiao.util.tip("设置失败，可能是无效帐号", d.errCode);
			} else {
				btiao.firstPage.curPosInfo.ownerUser = newOwnerId;
				$("#posOwner").text(btiao.firstPage.curPosInfo.ownerUser);
				$.mobile.changePage("#pgFirst");
			}
		}
	);
}

function MoreActionPage () {}
MoreActionPage.prototype.prepare = function() {
	if (btiao.firstPage.curPosInfo.ownerUser != btiao.loginMgr.user) {
		$("#actEnterChgPosOwnerPage").css("display", "none");
	} else {
		$("#actEnterChgPosOwnerPage").css("display", "block");
	}
}
MoreActionPage.prototype.actSetHomePage = function() {
	var obj = {};
	obj.positionId = btiao.firstPage.curPosInfo.id;
	var url = productRoot + "/usrInfoExt/" + btiao.loginMgr.user;
	btiao.util.postObj(url, obj, function(d){
		if (d.errCode == 101) {
			btiao.util.tipOver('还未设置个人信息', 0, function(){
				btiao.usrExtInfoPage.prepare();
			});
		} else if (d.errCode != 0) {
			btiao.util.tipOver('设置“家”位置失败', d.errCode, function(){
				$.mobile.changePage("#pgFirst");
			});
		} else {
			btiao.usrExtInfoPage.usrExtInfo.positionId = obj.positionId;
			$.mobile.changePage("#pgFirst");
		}
	});
}

btiao.reg("firstPage", new FirstPage());
btiao.reg("detailPage", new DetailPage());
btiao.reg("purchasePage", new PurchasePage());
btiao.reg("orderListPage", new OrderListPage());
btiao.reg("orderDetailPage", new OrderDetailPage());
btiao.reg("updateInfoPage", new UpdateInfoPage());
btiao.reg("usrExtInfoPage", new UsrExtInfoPage());
btiao.reg("subPosPage", new SubPosPage());
btiao.reg("chgPosOwnerPage", new ChgPosOwnerPage());
btiao.reg("moreActionPage", new MoreActionPage());

})();

