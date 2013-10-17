(function(){
	//global variable declare
	var productRoot = "/btiao/product";
	var positionId = 0;
	var numPer = 10;
	
	//function definition and init-code
	btiao.preparePgFirst = function(posId) {
		var loginUser = btiao.loginInfo.user;
		var token = btiao.loginInfo.token;
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
				
				fillBlockInfo(0, numPer);
				
				positionId = posId;
				$.mobile.changePage("#pgFirst");
			}
		});
	}
	
	function fillBlockInfo(lastInfoId, num) {
		$.ajax({
			type: "GET",
			url: productRoot+"/positions/"+posId+"/infos",
			contentType: "application/json; charset=UTF-8",
			data: {
				__opUsrInfo: {uId: loginUser, token: token},
				func: "normal",
				lastId: lastInfoId,
				num: num
			},
			success: function(d) {
				for d
			}
		});
			}
	}
	
})();
