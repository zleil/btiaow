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
}

