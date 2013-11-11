btiao = new Object();
btiao.reg = function(fullNS, value) {
	if (eval("!!"+fullNS)) {
		throw new Exception("redefined a global btiao value!");
	}
	
	var nsArray = fullNS.split('.');
	var sEval = "";
	var sNS = "btiao";
	
	for (var i = 0; i < nsArray.length; i++) {
		sNS += ".";
		sNS += nsArray[i];
		sEval += "if (typeof(" + sNS + ") == 'undefined') " + sNS + " = new Object();"
	}
	
	if (sEval != "") eval(sEval);
	eval(fullNS+"=value");
}

