var managmine = {};
managmine.states = [];
managmine.usrId = "";

function subStatesNum() {
	var num = $("#total").text();
	--num;
	$("#total").text(num);
}

function act_switched(stateid) {
	if (!confirm("朋友，你确认此物品已经成交了吗？")) {
		return;
	}
	
	var args = "{\"act\":\"stateChange\",data:{\"stateid\":"+stateid+"}}";
	
	$.ajax({
		type: "POST",
		data: args,
		url:"../api",
		success: function(msg) {
			eval("var ret = " + msg);
		    //alert(ret.errcode);
		    
			$("#"+getStateElmId(stateid)).remove();
		    
			subStatesNum();
		}
	});
}

function act_canceled(stateid) {
	if (!confirm("朋友，你确认不卖此物品了吗？")) {
		return;
	}
	
	var args = "{\"act\":\"stateChange\",data:{\"stateid\":"+stateid+"}}";
	$.ajax({
		type: "POST",
		data: args,
		url:"../api",
		success: function(msg) {
			eval("var ret = " + msg);
		    //alert(ret.errcode);
		    
		    $("#"+getStateElmId(stateid)).remove();
		    
		    subStatesNum();
		}
	});
}

function getStateElmId(stateId) {
	return "stateId_"+stateId;
}

function initStates() {
	for (var i=0; i<managmine.states.length; ++i) {
		var state = managmine.states[i];

		var one = '<div class="mystate" data-role="collapsible" id="'+getStateElmId(state.id)+'">' +
			'<h3>'+state.infos[0].content+'</h3>' +
			'<a href="#" class="ui-btn ui-corner-all" onclick="act_switched('+state.id+')">已成交</a>' +
			'<a href="#" class="ui-btn ui-corner-all" onclick="act_canceled('+state.id+')">不交易了</a>';
		for (var j=0; j<state.infos.length; ++j) {
			if (state.infos[j].t == 1) {
				one += '<p>'+state.infos[0].content+'</p>';
			} else if (state.infos[j].t == 2) {
				one += '<div class="image"><img src="' + state.infos[j].content + '"></div>';
			}
		}
		one += '</div>';
		$("#statesView").append(one);
	}
	
	$(".mystate").collapsible({ collapsed: true });
}

window.onload = function() {
	initStates();
}
