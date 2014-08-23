var managmine = {};
managmine.states = [];
managmine.usrId = "";

function act_switched(stateid) {
	var args = "{\"act\":\"stateChg\",data:{\"stateId\":\""+stateid+ "\"" +
	"}}";
	
	$.ajax({
		type: "POST",
		data: args,
		url:"../api",
		success: function(msg) {
			eval("var ret = " + msg);
		    //alert(ret.errcode);
		    
		    $("#state_"+stateid).remove();
		    
		    updateStateNum();
		}
	});
}

function act_canceled(stateid) {
	var args = "{\"areaId\":"+areaId+
		",\"wxuid\":\""+uid+"\""+
		",\"wxtoken\":\""+token+"\""+
		",\"act\":"+"\"deleteState\""+
		",\"stateid\":"+stateid+
		"}";
	$.ajax({
		type: "POST",
		data: args,
		url:"../api",
		success: function(msg) {
			eval("var ret = " + msg);
		    //alert(ret.errcode);
		    
		    $("#state_"+stateid).remove();
		    
		    updateStateNum();
		}
	});
}

function initStates() {
	for (var i=0; i<managmine.states.length; ++i) {
		var state = managemin.states[i];
		
	}
}

window.onload = function() {
	initStates();
}
