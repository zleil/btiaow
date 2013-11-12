(function(){
//global variable declare
var productRoot = "/btiao/product";
var numPer = 10;
	
//function definition and init-code
function FirstPage() {
	this.curPosInfo = undefined;
	this.usrExtInfo = undefined;
	this.infos = {};
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
			
			$("#actPurchase").click(function(){btiao.purchasePage.purchase()});
			
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
			this.infos = {};
			btiao.util.clearListViewData("#lstBlockInfo");
			
			for (var idx in d.content) {
				btiao.firstPage.fillAInfo(d.content[idx]);
			}
			$(".blockInfo").click(function(e){
				var infoId = $(e.target).attr("data-infoid");
				btiao.detailPage.prepare(infoId);
			});
			
			$.mobile.changePage("#pgFirst");
			$("#lstBlockInfo").listview("refresh");
		} else {
			btiao.log.l("error:"+d.errCode);
		}
	}, num, lastInfoId);
}
FirstPage.prototype.fillAInfo = function (info) {
	$("#lstBlockInfo").append(
			'<li data-theme="e">' +
			'<a class="blockInfo" data-infoid="'+info.id+'" data-transition="slide">' +
			info.desc +
			'</a></li>'
	);
	this.infos[info.id] = info;
}
FirstPage.prototype.goToOrderListPage = function(){
	btiao.orderListPage.prepare(this.curPosInfo.id);
}

function DetailPage() {
	this.info = null;
}
DetailPage.prototype.prepare = function (infoId) {
	this.info = btiao.firstPage.infos[infoId];

	$("#idInfoDesc").text(this.info.desc);
	$("#idInfoPrice").text(btiao.util.decoratePrice(this.info.price));
	$("#actEnterPurchase").click(function(){
		btiao.purchasePage.prepare(btiao.detailPage.info);
	});

	$.mobile.changePage("#pgDetail");
}

function PurchasePage () {
	this.info = undefined;
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
	
	$.mobile.changePage("#pgPurchase");//, {role:"dialog"});
}
PurchasePage.prototype.changePurchaseNum = function() {	
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
		
		var obj = {
				id: orderId,
				posId: btiao.firstPage.curPosInfo.id,
				productId: btiao.detailPage.info.id,
				productDesc: btiao.detailPage.info.desc,
				productNum: productNum,
				totalPrice: btiao.detailPage.info.price*productNum,
				fromUser: btiao.loginMgr.user,
				orderDst: orderDst
		}
		btiao.util.putObj(url, obj, function(d) {
			btiao.purchasePage.undispPurchaseAlert();
			btiao.purchasePage.setLastOrderDst();
			
//			$("#pgPurchase").dialog("close");
//					setTimeout(function(){
//						$("#pgPurchase").dialog("close");
//					},800);
			$.mobile.changePage("#pgFirst");
		});
	});
}

function OrderListPage() {
	this.orders = {};
	this.posId = "";
}
OrderListPage.prototype.prepare = function(posId) {
	this.posId = posId;
	
	this.fillAllOrder("", numPer);
}
OrderListPage.prototype.fillAllOrder = function (lastOrderId, num) {
	var url = productRoot+"/positions/"+this.posId+"/orders";
	btiao.util.getAllObj(url, function(d){
		if (d.errCode == 0) {
			if (d.content.length == 0) {
				$("#pgOrderListPage .noOrderTip").css("display", "block");
			} else {
				$("#pgOrderListPage .noOrderTip").css("display", "none");
			}
			
			for (var idx in d.content) {
				btiao.orderListPage.fillAOrder(d.content[idx]);
			}
			
			$(".orderInfo").click(function(e){
				var orderId = undefined;
				var i=5;
				var n = e.target;
				while (orderId == undefined && i-->0 && n != null) {
					orderId = $(n).attr("data-orderid");
					n = n.parentNode;
				}
				
				var orderInfo = btiao.orderListPage.orders[orderId];
				btiao.orderDetailPage.prepare(orderInfo);
			});
			
			$.mobile.changePage("#pgOrderListPage");
			$("#lstOrderInfo").listview("refresh");
		} else {
			btiao.log.l("error:"+d.errCode);
		}
	}, num, lastOrderId);
}
OrderListPage.prototype.fillAOrder = function(orderInfo) {
	$("#lstOrderInfo").append(
			'<li data-theme="e">' +
			'<a class="orderInfo" data-orderid="'+orderInfo.id+'" data-transition="slide">' +
			'<h3>' + orderInfo.productDesc + '</h3>' +
			'<p>总价: ' + orderInfo.totalPrice + '，数量：' + orderInfo.productNum + '送货位置：' + orderInfo.orderDst + '</p>' +
			'</a></li>'
	);
	this.orders[orderInfo.id] = orderInfo;
}

function OrderDetailPage() {
	this.orderInfo = null;
}
OrderDetailPage.prototype.prepare = function (orderInfo) {
	this.orderInfo = orderInfo;
	$("#labOrderId").text(this.orderInfo.id);
	
	$.mobile.changePage("#pgOrderDetailPage");
}

btiao.reg("firstPage", new FirstPage());
btiao.reg("detailPage", new DetailPage());
btiao.reg("purchasePage", new PurchasePage());
btiao.reg("orderListPage", new OrderListPage());
btiao.reg("orderDetailPage", new OrderDetailPage());

})();

