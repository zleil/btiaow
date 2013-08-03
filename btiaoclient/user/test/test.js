
function test() {
	$.ajax({
		type: "PUT"
		url: "/btiao/users/zl",
		content-type: "application/json,"
		data: {
			
		},
		success: function(d) {
			$("#idOut").append("result="+d.result);
		}
	})
}


function onload() {
	$("#idTest").click(test);
}
window.onload = onload;
