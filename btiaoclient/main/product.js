(function(){
	//global variable declare
	var productRoot = "/btiao/product";
	var positionId = 0;
	var numPer = 10;
	var loginUser = "";
	var token = "";
	var infos = {};
	var curInfoId = "";
	
	//function definition and init-code
	btiao.preparePgFirst = function(posId) {
		loginUser = btiao.loginInfo.user;
		token = btiao.loginInfo.token;
		$.ajax({
			type: "GET",
			url: productRoot+"/positions/"+posId,
			contentType: "application/json; charset=UTF-8",
			data: {
				__opUsrInfo: {uId: loginUser, token: token}
			},
			success: function(d) {
				var h3 = $("#pgFirst > div > h3");
				h3.empty();
				h3.append(d.content.name);
				
				var h5 = $("#pgFirst > div > div > div > h5");
				h5.empty();
				h5.append(d.content.desc);
				
				var img = $("#pgFirst > div > div > div > div > img");
				img.attr("src", d.content.imgUrl);
				
				positionId = posId;
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
				'<a href="#" class="blockInfo" data-infoid="'+info.id+'" data-transition="slide">' +
				info.desc +
				'</a></li>'
		);
		infos[info.id] = info;
		
		$("#lstBlockInfo li:last-child a").click(function(e){
			var infoId = e.target.dataset['infoid'];
			//alert(infoId);
			prepareBlockInfoDetail(infoId);
		});
	}
	function decoratePrice(price) {
		return (price*1.0)/100;
	}
	function prepareBlockInfoDetail(infoId) {
		var info = infos[infoId];
		
		$("#idInfoDesc").text(info.desc);
		$("#idInfoPrice").text("价格("+decoratePrice(info.price)+")");
		
		curInfoId = infoId;
		$.mobile.changePage("#pgDetail");
	}
	
	btiao.purchaseAddOne = function() {
		var num=parseInt($('#labProductNum').text()) + 1;
		$('#labProductNum').text(num);
		$('#labTotalPrice').text(decoratePrice(infos[curInfoId].price)*num);
	}
	btiao.purchaseSubOne = function() {
		var num=parseInt($('#labProductNum').text()) - 1;
		if (num < 0) {
			num = 0;
			return;
		}
		
		$('#labProductNum').text(num);
		$('#labTotalPrice').text(decoratePrice(infos[curInfoId].price)*num);
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
			var productNum = parseInt($('#labProductNum').text());
	
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
					$.mobile.changePage("#pgDetail");
				},
				error: function(httpReq, textStatus, errorThrow) {
					alert("购买失败，请确认网络无故障，若无故障则可能是系统问题:"+textStatus);
				}
			});
		});
	}
})();
