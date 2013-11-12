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
			
			$.mobile.changePage("#pgFirst");
			btiao.firstPage.fillBlockInfo("", numPer);
		});
	});
}
FirstPage.prototype.fillBlockInfo = function (lastInfoId, num) {
	var url = productRoot+"/positions/"+this.curPosInfo.id+"/infos";
	btiao.util.getAllObj(url, function(d){
		if (d.errCode == 0) {
			for (var idx in d.content) {
				btiao.firstPage.fillAInfo(d.content[idx]);
			}
			$(".blockInfo").click(function(e){
				var infoId = $(e.target).attr("data-infoid");
				btiao.detailPage.prepare(infoId);
			});
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
		
		var obj = {
				orderId: orderId,
				curPosId: btiao.firstPage.curPosInfo.id,
				productId: btiao.detailPage.info.id,
				productDesc: btiao.detailPage.desc,
				productNum: productNum,
				totalPrice: btiao.detailPage.info.totalPrice*productNum,
				fromUser: btiao.loginMgr.user
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

function OrderListPage() {}
OrderListPage.prototype.

btiao.reg("firstPage", new FirstPage());
btiao.reg("detailPage", new DetailPage());
btiao.reg("purchasePage", new PurchasePage());

})();

