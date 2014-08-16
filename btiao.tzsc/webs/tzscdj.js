var selDialog = new SelDialog("selectPos");
var phoneDialog = new PhoneDialog();

function PhoneDialog() {
	this.telId = "";
	this.nums = 0;
	$("#selTelId").html("&nbsp;");
}
PhoneDialog.prototype.clear = function() {
	this.telId = "";
	this.nums = 0;
	$("#selTelId").html("&nbsp;");
}
PhoneDialog.prototype.finish = function() {
	$("#telId").text($("#selTelId").text());
}
PhoneDialog.prototype.addOne = function(id) {
	if (this.nums == 11) {
		return;
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
	this.ret = ret;
	$( "#" + this.id ).dialog( "close" );
	
	if (this.selWhat == "selBuild") {
		$("#buildId").text(this.ret);
	} else if (this.selWhat == "selFloor") {
		$("#floorId").text(this.ret);
	} else if (this.selWhat == "selRoom") {
		$("#roomId").text(this.ret);
	}
}

SelDialog.prototype.setSelNum = function(num) {
	var rows = num / 5;
	var lastNumOfRow = num % 5;
	
	var tb = $("#seectPos_tb");
	tb.empty();
	
	var idx = 1;
	for (var i=1; i <= rows; ++i) {
		tb.append('<div class="ui-block-a" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
		tb.append('<div class="ui-block-b" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
		tb.append('<div class="ui-block-c" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
		tb.append('<div class="ui-block-d" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
		tb.append('<div class="ui-block-e" data-rel="back" onclick="selDialog.close('+idx+');"><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
	}
	
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-a" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-b" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-c" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-d" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
	if (lastNumOfRow-- > 0) tb.append('<div class="ui-block-e" data-rel="back" onclick="selDialog.close('+idx+')";><div class="ui-bar ui-bar-a tzscdj-selgrid">'+(idx++)+'</div></div>');
}

SelDialog.prototype.onSelDialog = function(selWhat) {
	selRet = -1;
	if (selWhat == "selBuild") {
		this.setSelTitle("选择楼号");
		this.setSelNum(21);
	} else if (selWhat == "selFloor") {
		this.setSelTitle("选择楼层");
		this.setSelNum(25);
	} else if (selWhat == "selRoom") {
		this.setSelTitle("选择房间号");
		this.setSelNum(10);
	}
	this.selWhat = selWhat;
	
	return true;
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
}
