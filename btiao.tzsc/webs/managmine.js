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

function updateStateNum() {
	var num = $("#stateNum").text();
	--num;
	$("#stateNum").text(num);
}