$(function(){
	var dispSelPanel = false;
	
	window.onload = function() {
		$("#actSelPanel").click(function(){
			if (dispSelPanel) {
				$("#actSelPanel").text("<<");
				$("#page").css("left", "0");
				$("#selPanel").css("display", "none");
				dispSelPanel = false;
			} else {
				$("#actSelPanel").text(">>");
				$("#page").css("left", "-80%");
				$("#selPanel").css("display", "block");
				dispSelPanel = true;
			}
			
		});
	}
});