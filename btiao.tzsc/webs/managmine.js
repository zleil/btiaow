var managmine = {};
managmine.states = [];
managmine.usrId = "";

function subStatesNum() {
	var num = $("#total").text();
	--num;
	$("#total").text(num);
}

function act_switched(stateid) {
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
			'<p>'+state.infos[0].content+'</p>' +
			'<a href="#" class="ui-btn ui-corner-all" onclick="act_switched('+state.id+')">已成交</a>' +
			'<a href="#" class="ui-btn ui-corner-all" onclick="act_canceled('+state.id+')">不交易了</a>' +
			'</div>';
		$("#statesView").append(one);
	}
	
	$(".mystate").collapsible({ collapsed: true });
}

window.onload = function() {
	initStates();
}
