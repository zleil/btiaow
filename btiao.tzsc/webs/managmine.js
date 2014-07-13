function act_switched(stateid) {
	var args = "{\"areaId\":"+areaId+
		",\"wxuid\":"+uid+
		",\"wxtoken\":"+token+
		",\"act:\""+"\"successSwitch\""+
		"}";
	htmlobj=$.ajax({
		type: "POST",
		data: args,
		url:"../api",
		success: function(msg) {
		     alert(msg);
		}
	});
}

function act_canceled(stateid) {
	
}