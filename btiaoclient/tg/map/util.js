(function() {
btiao.ns.reg("btiao.map.util.loadMapJs");
btiao.ns.reg("btiao.map.util.inited");

var key = "2E74E43c5b6863ea1b562f67fa3f7743";

/**
 * 异步加载地图js
 * @param callbackFuncName 回调函数名。
 */
btiao.map.util.loadMapJs = function () {
	function callbackFuncName() {
		btiao.map.util.inited = true;
	}
	var script = document.createElement("script");
	if (typeof callbackFuncName != undefined) {
		script.src = "http://api.map.baidu.com/api?v=1.5&ak="+key+"&callback="+callbackFuncName;
	} else {
		script.src = "http://api.map.baidu.com/api?v=1.5&ak="+key;
	}
	document.body.appendChild(script); 
}

btiao.map.util.MapJs = "http://api.map.baidu.com/api?v=1.5&ak="+key;
})();