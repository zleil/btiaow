(function(){
	//global variable declare
	var productRoot = "/btiao/product";
	var positionId = 0;
	var positionName = "";
	var numPer = 10;
	var loginUser = "";
	var token = "";
	var infos = {};
	var curInfoId = "";
	var usrExtInfo;
	
	//function definition and init-code
	function getObj(url, func) {
		$.ajax({
			type: "GET",
			url: "http://localhost"+url,
			contentType: "application/json; charset=UTF-8",
			data: {__opUsrInfo:{uId: loginUser, token: token}},
			success: func
		});
	}
	function getUsrExtInfo(func) {
		var url = productRoot + "/usrInfoExt/" + loginUser;
		return getObj(url, func);
	}
	
	
	btiao.preparePgFirst = function(posId) {
		loginUser = btiao.loginInfo.user;
		token = btiao.loginInfo.token;
		
		getUsrExtInfo(function(d){
			usrExtInfo = d.content;
			preparePgFirst2(posId);
		});
	}
	function preparePgFirst2(posId) {
		$.ajax({
			type: "GET",
			url: productRoot+"/positions/"+posId,
			contentType: "application/json; charset=UTF-8",
			data: {
				__opUsrInfo: {uId: loginUser, token: token}
			},
			success: function(d) {
				$("#actPurchase").click(btiao.purchase);
				
				positionName = d.content.name;
				positionId = posId;
				
				var h3 = $(".posName");
				h3.empty();
				h3.append(d.content.name);
				
				fillBlockInfo("", numPer);
				
				$.mobile.changePage("#pgFirst");
			}
		});
	}
	
	function fillBlockInfo(lastInfoId, num) {
		$.ajax({
			type: "GET",
			url: productRoot+"/positions/"+positionId+"/infos",
			contentType: "application/json; charset=UTF-8",
			data: {
				__opUsrInfo: {uId: loginUser, token: token},
				func: "normal",
				lastId: lastInfoId,
				num: num
			},
			success: function(d) {
				if (d.errCode == 0) {
					for (var idx in d.content) {
						fillAInfo(d.content[idx]);
					}
					$(".blockInfo").click(function(e){
						var infoId = $(e.target).attr("data-infoid");
						prepareBlockInfoDetail(infoId);
					});
					$("#lstBlockInfo").listview("refresh");
				} else {
					//TODO
					alert("error:"+d.errCode);
				}
			}
		});
	}
	
	function fillAInfo(info) {
		$("#lstBlockInfo").append(
				'<li data-theme="e">' +
				'<a class="blockInfo" data-infoid="'+info.id+'" data-transition="slide">' +
				info.desc +
				'</a></li>'
		);
		infos[info.id] = info;
	}
	function decoratePrice(price) {
		return (price*1.0)/100;
	}
	function prepareBlockInfoDetail(infoId) {
		var info = infos[infoId];
		
		$("#pgDetail .posName").text(positionName);
		$("#idInfoDesc").text(info.desc);
		$("#idInfoPrice").text(decoratePrice(info.price));
		$("#actEnterPurchase").click(btiao.preparePurchasePage);
		
		curInfoId = infoId;
		$.mobile.changePage("#pgDetail");
	}
	
	btiao.preparePurchasePage = function() {
		$('#labProductNum').val(1);
		$('#labProductNum').slider("refresh");
		btiao.changePurchaseNum();
		
		$("#orderDst").val(usrExtInfo.locationOfPos);
		$.mobile.changePage("#pgPurchase");//, {role:"dialog"});
	}
	
	btiao.changePurchaseNum = function() {
		if (!curInfoId || curInfoId == "") return;
		
		var num = parseInt($('#labProductNum').val());
		
		var info = infos[curInfoId];
		var price = info.price;
		var truePrice = decoratePrice(price);
		$('#labTotalPrice').text(truePrice*num);
	}
	
	function getId(idName, func) {
		$.ajax({
			type: "POST",
			url: productRoot+"/getId/"+idName,
			contentType: "application/json; charset=UTF-8",
			data: '{\
				__opUsrInfo: {uId: "' + loginUser + '", token: "' + token + '"} \
			}',
			success: function(d) {
				var idValue = d.content.nextValue;
				(func)(idValue);
			}
		});
	}
	
	btiao.purchase = function() {
		getId("orderId", function(orderId) {
			var productNum = parseInt($('#labProductNum').val());
	
			$.ajax({
				type: "PUT",
				url: productRoot+"/positions/"+positionId+"/orders/__n",
				contentType: "application/json; charset=UTF-8",
				data: '{ \
					__opUsrInfo: {uId: "' + loginUser + '", token: "' + token + '"}, \
					orderId: "'+orderId+'", \
					posId: "'+positionId+'", \
					productId: "'+curInfoId+'", \
					productDesc: "'+infos[curInfoId].desc+'", \
					productNum: "'+productNum+'", \
					totalPrice: "'+infos[curInfoId].totalPrice*productNum+'", \
					fromUser: "'+loginUser+'" \
				}',
				success: function(d) {
					//$("#pgPurchase").dialog("close");
//					setTimeout(function(){
//						$("#pgPurchase").dialog("close");
//					},800);
					$.mobile.changePage("#pgFirst");
				},
				error: function(httpReq, textStatus, errorThrow) {
					alert("购买失败，请确认网络无故障，若无故障则可能是系统问题:"+textStatus);
				}
			});
		});
	}
})();

