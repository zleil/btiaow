$(function(){
	var dispSelPanel = false;
	
	function OpPanel(id) {
		this.opPanelId = id;
		
		var self = this;
		$(".selOption").click(function(e) {
			var idPanel = btiao.opPanel.opPanelId;
			
			var sels = $("#"+id+" .selOption");
			for (var i=0; i<sels.length; ++i) {
				if (e.target.id == sels[i].id) continue;
				
				var elmOption = $(sels[i]);
				elmOption.removeClass("selOptionSel");
				elmOption.addClass("selOptionUnSel");
				
				var idSubPanel = "#" + sels[i].dataset.for;
				$(idSubPanel).css("display", "none");
			}
			
			var ts = $(e.target);
			ts.removeClass("selOptionUnSel");
			ts.addClass("selOptionSel");
			
			var idSubPanel = "#" + e.target.dataset.for;
			$(idSubPanel).css("display", "block");
			
			self.clickOpMode(e.target.id);
		});
	}
	OpPanel.prototype.clickOpMode = function(id) {
		
	}
	
	btiao.reg("opPanel", new OpPanel("idOpPanel"));
	
	window.onload = function() {
		$("#actSelPanel").click(function(){
			if (dispSelPanel) {
				$("#actSelPanel").text("<<");
				$("#page").css("left", "0");
				$("#idOpPanel").css("display", "none");
				dispSelPanel = false;
			} else {
				$("#actSelPanel").text(">>");
				$("#page").css("left", "-80%");
				$("#idOpPanel").css("display", "block");
				dispSelPanel = true;
			}
			
		});
	}
});