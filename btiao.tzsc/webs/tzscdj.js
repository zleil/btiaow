var selDialog = new SelDialog("selectPos");
var phoneDialog = new PhoneDialog();

function PhoneDialog() {
	this.telId = "";
	this.nums = 0;
	$("#selTelId").html("_");
}
PhoneDialog.prototype.clear = function() {
	this.telId = "";
	this.nums = 0;
	$("#selTelId").html("_");
}
PhoneDialog.prototype.finish = function() {
	$("#telId").text($("#selTelId").text());
}
PhoneDialog.prototype.addOne = function(id) {
	if (this.nums == 11) {
		return;
	}
	
	if (this.nums == 0) {
		$("#selTelId").html("");
	}
	
	$("#selTelId").append(id);
	this.telId += id;
	++this.nums;
	
	if (this.nums == 3 || this.nums == 7) {
		$("#selTelId").append(" - ");
	}
}
PhoneDialog.prototype.delOne = function() {
	if (this.telId.length == 0) return;
	
	this.telId = this.telId.substring(0, this.telId.length-1);
	--this.nums;
	
	if (this.telId.length == 3 || this.telId.length == 7) {
		this.telId = this.telId.substring(0, this.telId.length-1);
		--this.nums;
	}
	
	var telId = this.telId;
	this.clear();
	
	for (var i=0; i<telId.length; ++i) {
		this.addOne(telId.charAt(i));
	}
}

function SelDialog(id) {
	this.id = id;
	this.ret = -1;
}
SelDialog.prototype.setSelTitle = function(title) {
	$("#selectPos_Title").text(title);
}

SelDialog.prototype.close = function(ret) {
	this.ret = !!this.bRange?this.widthRange(ret):ret;
	$( "#" + this.id ).dialog( "close" );
	
	if (this.selWhat == "selBuild") {
		$("#buildId").text(this.ret);
	} else if (this.selWhat == "selFloor") {
		$("#floorId").text(this.ret);
	} else if (this.selWhat == "selRoom") {
		$("#roomId").text(this.ret);
	}
}
SelDialog.prototype.widthRange = function(num) {
	if (num < 10) {
		return "" + "0" + num;
	} else {
		return num;
	}
}
SelDialog.prototype.setSelNum = function(num) {
	var rows = num / 5;
	var lastNumOfRow = num % 5;
	
	var tb = $("#seectPos_tb");
	tb.empty();
	
	var idx = 1;
	for (var i=1; i <= rows; ++i) {
		tb.append('<div class="ui-block-a" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
		tb.append('<div class="ui-block-b" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
		tb.append('<div class="ui-block-c" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
		tb.append('<div class="ui-block-d" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
		tb.append('<div class="ui-block-e" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
	}
	
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-a" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-b" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-c" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-d" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-e" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(!!this.bRange?this.widthRange((idx++)):(idx++))+'</div></div>');
}

SelDialog.prototype.onSelDialog = function(selWhat) {
	if (selWhat == "selBuild") {
		this.bRange = false;
		this.setSelTitle("选择楼号");
		this.setSelNum(21);
	} else if (selWhat == "selFloor") {
		this.bRange = true;
		this.setSelTitle("选择楼层");
		this.setSelNum(25);
	} else if (selWhat == "selRoom") {
		this.bRange = true;
		this.setSelTitle("选择房间号");
		this.setSelNum(10);
	}
	this.selWhat = selWhat;
	
	return true;
}

function commonTip(tipStr, idSel, afterclose_func) {
	$(idSel).text(tipStr);
	
	if (typeof(afterclose_func) == "undefined") {
		$(idSel).popup({afterclose:afterclose_func});
	} else {
		$(idSel).popup({});
	}
	
	$(idSel).popup("open");
}

function closeWindowFunc() {
	if (typeof(WeixinJSBridge) != "undefined") {
		WeixinJSBridge.call("closeWindow");
	} else {
		window.close();
	}
}

function dengji() {
	var telId = $("#telId").text();
	telId = telId.replace(/-/g, "");
	telId = telId.replace(/ /g, "");
	
	var buildId = $("#buildId").text();
	var floorId = $("#floorId").text();
	var roomId = $("#roomId").text();
	
	if (!buildId.match(/^\d+$/) || !floorId.match(/^\d+$/) || !roomId.match(/^\d+$/)) {
		var tipStr = "您还没有输入完整的房间号呦~";
		commonTip(tipStr, "#commonTip");
		return;
	}
	
	if (!telId.match(/^\d{11}$/)) {
		var tipStr = "您的手机号好像不对呦~" + telId;
		commonTip(tipStr, "#commonTip");
		return;
	}
	
	var homeId = "" + buildId + "#" + floorId + roomId;
	
	var args = "{\"act\":\"dengji\",data:{\"homeId\":\""+homeId+ "\"" +
		",\"telId\":\""+telId+"\"" +
		"}}";
	$.ajax({
		type: "POST",
		data: args,
		url:"../api",
		success: function(msg) {
			eval("var ret = " + msg);

			if (ret.errcode == 0) {
				var tip = $("#dengjiOKTip").popup({afterclose: closeWindowFunc});
			    tip.popup("open");
			} else {
				if (ret.errcode == 100001) {
					var tipStr = "系统服务重启，请您重新从微信公共账号进入";
					commonTip(tipStr, "#commonTip", closeWindowFunc);
				} else {
					var tipStr = "服务忙，请稍后再登记：-）";
					commonTip(tipStr, "#commonTip");
				}
			}
		}
	});
}

window.onload = function() {
	$("#selDialog_build_bt").click(function() {
		selDialog.onSelDialog("selBuild");
	});
	$("#selDialog_floor_bt").click(function() {
		selDialog.onSelDialog("selFloor");
	});
	$("#selDialog_room_bt").click(function() {
		selDialog.onSelDialog("selRoom");
	});
	$("#dengji_bt").click(function() {
		dengji();
	})
}
