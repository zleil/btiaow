var admin.opUsrId = "";
var usrs = {};

function setOpPanel() {
	var uinfo = usr.get(admin.opUsrId);
	$("#telId").val("href", "tel:"+uinfo.telId);
	$("#telDesc").val("href", "tel:"+uinfo.telId);
}

function approve() {
	
}

function disagree() {
	
}

function initUsrs() {
	
}

window.onload = function() {
	initUsrs();
}

