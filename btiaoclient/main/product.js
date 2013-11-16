(function(){
//global variable declare
var productRoot = "/btiao/product";
var numPer = 10;
	
//function definition and init-code
function FirstPage() {
	this.curPosInfo = undefined;
	this.usrExtInfo = undefined;
	this.infos = {};
	this.lastId = "";
}
FirstPage.prototype.moreBlockInfos = function(){
	this.fillBlockInfo(this.lastId, numPer);
}
FirstPage.prototype.prepare = function() {
	var usrInfoExturl = productRoot + "/usrInfoExt/" + btiao.loginMgr.user;
	btiao.util.getObj(usrInfoExturl, function(d){
		var curPosId = undefined;
		if (d.errCode == 0) {
			this.usrExtInfo = d.content;
			curPosId = this.usrExtInfo.positionId;
		}
		
		if (curPosId == undefined) curPosId = "1000001";
		
		var posUrl = productRoot+"/positions/"+curPosId;
		btiao.util.getObj(posUrl, function(d){
			if (d.errCode != 0) {
				btiao.log.l("failed to get position info!");
				return;
			}
			
			btiao.firstPage.curPosInfo = d.content;
			
			var h3 = $(".posName");
			h3.empty();
			h3.append(btiao.firstPage.curPosInfo.name);
			
			$("#posOwner").text(btiao.firstPage.curPosInfo.owner);
			
			btiao.firstPage.fillBlockInfo("", numPer);
		});
	});
}
FirstPage.prototype.fillBlockInfo = function (lastInfoId, num) {
	var url = productRoot+"/positions/"+this.curPosInfo.id+"/infos";
	btiao.util.getAllObj(url, function(d){
		if (d.errCode == 0) {
			if (lastInfoId == "") {
				this.infos = {};
				btiao.util.clearListViewData("#lstBlockInfo");
			}
			
			for (var idx in d.content) {
				btiao.firstPage.fillAInfo(d.content[idx]);
			}
			
			$.mobile.changePage($("#pgFirst"));
			$("#lstBlockInfo").listview("refresh");
		} else {
			btiao.log.l("error:"+d.errCode);
		}
	}, num, lastInfoId);
}
FirstPage.prototype.onclickBlockInfo = function(elm) {
	var infoId = $(elm).attr("data-infoid");
	btiao.detailPage.prepare(infoId);
	return false;
}
FirstPage.prototype.fillAInfo = function (info) {
	var html = "";
	html += '<li data-theme="e">';
	html += '<a onclick="btiao.firstPage.onclickBlockInfo(this);" class="blockInfo" data-infoid="'+info.id+'" data-transition="slide">';
	html += '<h3>'+info.desc+'</h3>';
	html += '<p>现价：';
	html += '<span'+((info.price < info.oldPrice) ? ' class="newLowPrice">':'>')+btiao.util.decoratePrice(info.price)+'</span>';
	html += (info.price < info.oldPrice)? ('，<span class="oldHighPrice">原价：'+btiao.util.decoratePrice(info.oldPrice)+'</span>') : '';
	html += '</p>';
	html += '</a></li>';
	
	$("#lstBlockInfo").append(html);
	this.infos[info.id] = info;
	this.lastId = info.id;
}

function DetailPage() {
	this.info = null;
}
DetailPage.prototype.prepare = function (infoId) {
	this.info = btiao.firstPage.infos[infoId];

	$("#idInfoDesc").text(this.info.desc);
	$("#idInfoPrice").text(btiao.util.decoratePrice(this.info.price));

	$.mobile.changePage($("#pgDetail"));
}

function PurchasePage () {
	this.info = null;
}
PurchasePage.prototype.prepare = function(info) {
	this.info = info;
	
	$('#labProductNum').val(1);
	$('#labProductNum').slider("refresh");
	this.changePurchaseNum();
	
	var lastDst = btiao.clientPersist.get("btiao.orderDst");
	var usrExtInfo = btiao.firstPage.usrExtInfo;
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
	
	$.mobile.changePage($("#pgPurchase"));
}
PurchasePage.prototype.changePurchaseNum = function() {
	if (this.info == null) return;
	
	var num = parseInt($('#labProductNum').val());
	var price = this.info.price;
	var truePrice = btiao.util.decoratePrice(price);
	$('#labTotalPrice').text(truePrice*num);
}
PurchasePage.prototype.checkOrderDst = function () {
	return !!$("#orderDst").val().match(/[0-9]{8}/);
}
PurchasePage.prototype.dispPurchaseAlert = function () {
	$("#orderDstNote").addClass("alertArea");
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
		var createTime = new Date().valueOf();
		var obj = {
				id: orderId,
				posId: btiao.firstPage.curPosInfo.id,
				productId: btiao.detailPage.info.id,
				productDesc: btiao.detailPage.info.desc,
				productNum: productNum,
				totalPrice: btiao.detailPage.info.price*productNum,
				fromUser: btiao.loginMgr.user,
				orderDst: orderDst,
				createTime: createTime
		}
		btiao.util.putObj(url, obj, function(d) {
			btiao.purchasePage.undispPurchaseAlert();
			btiao.purchasePage.setLastOrderDst();
			
			$.mobile.changePage($("#pgFirst"));
		});
	});
}

function OrderListPage() {
	this.orders = {};
	this.posId = "";
	this.lastOrderId = "";
	this.dispStyleTodo = "todo";
	this.dispStyleAll = "all";
	this.displayStyle = this.dispStyleTodo;
}
OrderListPage.prototype.clearView = function() {
	btiao.util.clearListViewData("#lstOrderInfo");
	$("#lstOrderInfo").listview("refresh");
}
OrderListPage.prototype.prepare = function(posId) {
	this.posId = posId;

	this.clearView();
	
	this.fillAllOrder("", numPer);
}
OrderListPage.prototype.actDisplayTodoOrder = function() {
	if (this.displayStyle == this.dispStyleTodo) return;
	
	this.displayStyle = this.dispStyleTodo;
	$("#actDisplayAllOrder").attr("checked","checked");
	$("#actDisplayTodoOrder").removeAttr("checked");
	
	this.clearView();
	
	this.prepare(this.posId);
}
OrderListPage.prototype.actDisplayAllOrder = function() {
	if (this.displayStyle == this.dispStyleAll) return;
	
	this.displayStyle = this.dispStyleAll;
	$("#actDisplayAllOrder").attr("checked","checked");
	$("#actDisplayTodoOrder").removeAttr("checked");
	
	this.clearView();
	
	this.prepare(this.posId);
}
OrderListPage.prototype.moreOrders = function() {
	this.fillAllOrder(this.lastOrderId, numPer);
}
OrderListPage.prototype.fillAllOrder = function (lastOrderId, num) {
	var url = productRoot+"/positions/"+this.posId+"/orders";
	btiao.util.getAllObj(url, function(d){
		if (d.errCode == 0) {
			if (lastOrderId == "" && d.content.length == 0) {
				$("#pgOrderListPage .noOrderTip").css("display", "block");
			} else {
				$("#pgOrderListPage .noOrderTip").css("display", "none");
			}
			
			if (d.content.length == 0) {
				$("#actMoreOrders").buttonMarkup("disable");
				$("#actMoreOrders").buttonMarkup("refresh");
			}
			
			for (var idx in d.content) {
				btiao.orderListPage.fillAOrder(d.content[idx]);
			}
			
			$.mobile.changePage($("#pgOrderListPage"));
			$("#lstOrderInfo").listview("refresh");
		} else {
			btiao.log.l("error:"+d.errCode);
		}
	}, num, lastOrderId);
}
OrderListPage.prototype.onclickOrderInfo = function(elm) {
	var orderId = undefined;
	var i=5;
	var orderId = btiao.util.getSpecificUpperElm(elm, "data-orderid");
	
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
	$("#lstOrderInfo").append(
			'<li data-theme="e" data-orderid="'+orderInfo.id+'">' +
			'<a onclick="btiao.orderListPage.onclickOrderInfo(this);" class="orderInfo data-transition="slide">' +
			'<h3>' + orderInfo.productDesc + '</h3>' +
			'<p><span class="state '+this.getOrderStateClass(orderInfo.state)+'">'+this.getStateDesc(orderInfo.state)+'</span>，订购时间： ' + btiao.util.decorateTime(orderInfo.createTime) + '</p>' +
			'<p>总价： ' + totalPrice + '元，数量：' + orderInfo.productNum + '，送货位置：' + orderInfo.orderDst + '</p>' +
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
	var orderId = btiao.util.getSpecificUpperElm(elm, "data-orderid");
	var url = productRoot+"/positions/"+btiao.firstPage.curPosInfo.id+"/orders/"+orderId;
	var obj = {
			id: orderId,
			state:"2"
	}
	
	var callback = function(d) {
		var stateE = $(".state",callback.elm.parentNode);
		stateE.removeClass(btiao.orderListPage.getOrderStateClass(0));
		stateE.addClass(btiao.orderListPage.getOrderStateClass(2));
		
		$(callback.elm).remove();
		stateE.text(btiao.orderListPage.getStateDesc(2));
		
		$("#lstOrderInfo").listview("refresh");
		btiao.orderListPage.orders[callback.orderId].state = 2;
	}
	callback.elm = elm;
	callback.orderId =orderId;
	btiao.util.postObj(url, obj, callback);
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

btiao.reg("firstPage", new FirstPage());
btiao.reg("detailPage", new DetailPage());
btiao.reg("purchasePage", new PurchasePage());
btiao.reg("orderListPage", new OrderListPage());
btiao.reg("orderDetailPage", new OrderDetailPage());

})();

