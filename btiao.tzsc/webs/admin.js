var admin = {};
admin.opUsrId = "";
admin.usrs = {};

function setOpPanel() {
	var uinfo = usr.get(admin.opUsrId);
	$("#telId").val("href", "tel:"+uinfo.telId);
	$("#telDesc").val("href", "tel:"+uinfo.telId);
}

function approve() {
	var r = confirm("yes or no");
	alert(r);
}

function disagree() {
	
}

function initUsrs() {
	for (var usrId in admin.usrs) {
		var uinfo = admin.usrs[usrId];
		$("#usrlist").append('<li><a href="#operatePg" onclick="setOpPanel(\''+uinfo.usrId+'\')" data-rel="dialog">'+uinfo.nick+'('+uinfo.usrId+')</a></li>');
	}
}

window.onload = function() {
	initUsrs();
	
	$("#stopAllInput").click(function(){
		var r = confirm("yes or no");
		if (r) {
			var args = "{\"act\":\"stopAllInput\",data:{}";
			$.ajax({
				type: "POST",
				data: args,
				url:"../api",
				success: function(msg) {
					eval("var ret = " + msg);
	
					if (ret.errcode == 0) {
						alert("成功");
					} else {
						alert("erro="+ret.errcode+",msg="+ret.errdesc);
					}
				}
			});
		}
	});
}

