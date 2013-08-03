function onload() {
//namespace registor

//global variable
var uLon = 116425000;
var uLat = 40052000;
var posDesc = "";
btiao.tgGlobal = {};

var nextPageIdx = 0;
var page_size = 10;
var hasMore = true;

var recentTg = [];
var RECENT_LIMIT = 10;

var lastSelModeId = "idSelModeNormal";

var tgSvrRoot = "/btiao/tg";

//function define
function clearRecent() {
	recentTg = [];
	storeRecent();
	loadRecent();
}
function refreshPos() {
	var url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js";
	$.getScript(url, function() {
		var cityName = $("#curCity");
		cityName.empty();
		cityName.append(remote_ip_info.city);
		
		if (remote_ip_info.city == "北京") {
			btiao.tgGlobal.curTgCity = "beijing";
		}
		
		//当前仅支持北京，调试代码
		//TODO
		btiao.tgGlobal.curTgCity = "beijing";
		cityName.empty();
		cityName.append("北京");
	});
}

function genFilerArgs() {
	if (lastSelModeId == "idSelModeFD") {
		return '&tgType=1';
	} else if (lastSelModeId == "idSelModeDY") {
		return '&tgType=20000';
	} else {
		return "";
	}
}

function refreshTg(wantEmpty) {
	if (wantEmpty) {
		$("#idTgList").empty();
		nextPageIdx = 0;
		$("#idTgProgress").css("display","block");
		$("#idMoreTg").css("display","none");
	}
	
	if (!btiao.tgGlobal.curTgCity || btiao.tgGlobal.curTgCity == "") {
		setTimeout(function() {refreshTg(wantEmpty)}, 200);
		return;
	}
	
	var url = tgSvrRoot + "/GetTgs?pgs="+page_size+"&idx="+(nextPageIdx++)*page_size+"&city="+btiao.tgGlobal.curTgCity+
			"&uLon="+uLon+"&uLat="+uLat+genFilerArgs();
	
	$.getScript(url, function() {
		var result = rst.result;
		if (result != 0) {
			return;
		}
		
		$("#idNoTgTip").css("display", "none");
		if (wantEmpty) {
			if (rst.tgs.length > 0) {
				$("#idTgProgress").css("display","none");
				$("#idMoreTg").css("display","block");
			} else {
				$("#idNoTgTip").css("display", "block");
			}
		}
		for (var idx=0; idx<rst.tgs.length; ++idx) {
			var tgHtml = '<li class="cTgLi"><div class="'+(idx%2==0?'cTgUnitLeft':'cTgUnitRight')+'">';
			tgHtml += genTgHtml(rst.tgs[idx]);
			tgHtml += '</div></li>';
			$("#idTgList").append(tgHtml);
		}
		
		if (rst.tgs.length < page_size) {
			hasMore = false;
			
			$("#idMoreTg").empty();
			$("#idMoreTg").append("《您要的信息均已呈现》");
		} else {
			$("#idMoreTg").empty();
			$("#idMoreTg").append("还有更多...");
		}
	});
}

btiao.tgGlobal.insertRecent = insertRecent;
function insertRecent(name, url, title) {
	if (name.length > 18) {
		name = name.substring(0, 20) + "...";
	}

	for (var i=0; i<recentTg.length; ++i) {
		if (recentTg[i].name == name && recentTg[i].url == url) {
			return;
		}
	}
	
	var d = {name:name,url:url,title:title};
	recentTg.push(d);
	
	if (recentTg.length > RECENT_LIMIT) {
		recentTg = recentTg.slice(recentTg.length-RECENT_LIMIT,recentTg.length);
	}
	
	refreshRecent();
}
function refreshRecent() {
	$("#idAllLook").empty();
	for (var i=recentTg.length-1; i>=0&&i>(recentTg.length-RECENT_LIMIT); --i) {
		var html = '<p><a class="cRecentHref" target="_blank" title="'+ recentTg[i].title + '" href="'+ recentTg[i].url +'">';
		html += recentTg[i].name;
		html += '</a></p>';
		$("#idAllLook").append(html);
	}
	
	storeRecent();
}
function storeRecent() {
	if (localStorage) {
		localStorage["tg.recentTg"] = JSON.stringify(recentTg);
	}
}
function loadRecent() {
	if (localStorage) {
		var recent = localStorage["tg.recentTg"];
		if (recent) {
			recentTg = JSON.parse(recent);
			refreshRecent();
		}		
	}
}
function storeUserPos(){
	if (localStorage) {
		localStorage["tg.uLon"] = uLon;
		localStorage["tg.uLat"] = uLat;
		localStorage["tg.posDesc"] = posDesc;
	}
}
function loadUserPos(){
	if (localStorage) {
		var lon = localStorage["tg.uLon"];
		if (lon) uLon = lon;
		
		var lat = localStorage["tg.uLat"];
		if (lat) uLat = lat;
		
		var desc = localStorage["tg.posDesc"];
		if (desc) posDesc = desc;
	}
}

function genGoCode(tg) {
	var code = 'btiao.tgGlobal.insertRecent(';
	code += '\'' + tg.shopName + '\',';
	code += '\'' + tg.url + '\',';
	code += '\'' + tg.title + '\'';
	code += ')';
	return code;
}

function genTgHtml(tg) {
	var r = '<a onclick="'+genGoCode(tg)+'" target="_blank" href="';
	r += tg.url;
	r += '"><img class="ctgImg" src="';
	r += tg.imageUrl;
	r += '"/></a>';
	tg.desc = tg.desc.replace(/‘/g, "&apos;");
	tg.desc = tg.desc.replace(/”/g, "&quot;");
	tg.desc = tg.desc.replace(/>/g, "&gt;");
	tg.desc = tg.desc.replace(/</g, "&lt;");
	r += '<p class="ctgTitle"><span><a class="cTgTileHref" title="'+tg.desc+'" onclick="'+genGoCode(tg)+'" target="_blank" href="';
	r += tg.url;
	r += '">';
	var endIdx = tg.title.length > 60 ? 60 : tg.title.length;
	r += tg.title.substring(0, endIdx);
	r += '</a></span></p>';
	r += '<div class="cPriceLine"><p>';
	r += '<span class="cMoneySign">¥</span>';
	r += '<span class="cPrice">';
	r += tg.price/100;
	r += '</span>';
	r += '&nbsp;&nbsp;<span class="cValue">&nbsp;&nbsp;原价&nbsp;</span>';
	r += '<span class="cValue">¥';
	r += tg.value/100;
	r += '&nbsp;&nbsp;</span>';
	r += '<a onclick="'+genGoCode(tg)+'" class="cGo" target="_blank" href="';
	r += tg.url;
	r += '" title="'+tg.shopName+'">Go!&nbsp;看看</a></p>';
	var dist = distMode(tg.dist);
	if (dist == 0) {
		r += '<p class="cDist"><span>500&nbsp;米以内</span>';
	} else {
		r += '<p class="cDist"><span>大约'+distMode(tg.dist)+'&nbsp;米</span>';
	}
	r += '<span>&nbsp;|&nbsp;&nbsp;<a class="cCheckRoute" href="'+genLineUrl('北京',uLon,uLat,tg.longitude,tg.latitude,tg.shopName)+'" target="_blank" title="'+tg.shopName+'">查看大概路线</a></span>';
	r += '</p></div>'
		
	return r;
}
function distMode(dist) {
	return parseInt(dist/500)*500;
}
function genLineUrl(city, uLon, uLat, tgLon, tgLat, shopName) {
	return 'http://api.map.baidu.com/direction?origin=latlng:'+uLat/1000000+','+uLon/1000000+'|name:您的位置'+
		'&destination=latlng:'+tgLat/1000000+','+tgLon/1000000+'|name:'+shopName+'&output=html&mode=driving'+
		'&region='+city;
}

function scrollEvent(evt) {
	var viewportHeight = window.innerWidth;
	var verticalScroll = window.pageYOffset;

	var element = document.getElementById("idMoreTg");
	var actualTop = getY(element) + 665;
	
	if (viewportHeight >= (actualTop - verticalScroll)) {
		if (scrollEvent.lastMoreState == "display") {
			return;
		}
		
		scrollEvent.lastMoreState = "display";
		
		if (hasMore) {
			var second = 3;
			setTimeout(function() {
				$("#idMoreTg").empty();
				$("#idMoreTg").append("马上查询下一页... 倒计时（"+(second)+"）秒");
				if (second == 0) {
					refreshTg();
					$("#idMoreTg").empty();
					$("#idMoreTg").append("正在查询下一页... ");
				} else {
					setTimeout(arguments.callee, 1000);
				}
				
				-- second;
			}, 500);

			//alert("top="+actualTop+",hegiht="+viewportHeight+",verticalScroll="+verticalScroll)
		}
	} else {
		scrollEvent.lastMoreState = "hide";
	}
}
function getY(element) {
    var y = 0;
    for(var e = element; e; e = e.offsetParent) // Iterate the offsetParents
        y += e.offsetTop;                       // Add up offsetTop values

    for(e = element.parentNode; e && e != document.body; e = e.parentNode)
        if (e.scrollTop) y -= e.scrollTop;  // subtract scrollbar values
    return y;
}

btiao.tgGlobal.changePos = changePos;
function changePos() {
	if ($("#idChangePos").css("display") == "block") {
		$("#idChangePos").css("display", "none");
		changePos.displayed = false;
		return;
	}
	
	$("#idChangePos").css("display", "block");
	gMap = new BMap.Map("idMap");
	gMap.centerAndZoom(new BMap.Point(116.414, 39.915), 15);
	gMap.enableKeyboard();
	gMap.centerAndZoom(new BMap.Point(uLon/1000000, uLat/1000000), 15);
	gMap.enableScrollWheelZoom();
	gMap.setDefaultCursor("crosshair");
	gMap.setDraggingCursor("pointer");
	
	changePos.uLon = uLon/1000000;
	changePos.uLat = uLat/1000000;
	changePos.posDesc = posDesc;
	setUserPosSelBar(changePos.uLon, changePos.uLat, changePos.posDesc);
	
	gMap.addEventListener("click", function(e){
		changePos.uLon = e.point.lng;
		changePos.uLat = e.point.lat;
		setUserPosSelBar(changePos.uLon, changePos.uLat, "查询中...");
		
		var listGeo = new BMap.Geocoder();
		listGeo.getLocation(new BMap.Point(changePos.uLon, changePos.uLat), 
			function(result) {
				if (result) {
					changePos.posDesc = result.address;
					
					if (e.point.lng == changePos.uLon && e.point.lat == changePos.uLat) {
						//不相同就不要修改了，防止两次getLocation中最后一次先返回，造成显示异常
						setUserPosSelBar(e.point.lng, e.point.lat, changePos.posDesc);
					}
				}
			}
		);
	});
}
function setUserPosSelBar(lon, lat, desc) {
	$("#idSelPos").empty();
	$("#idSelPos").append(desc + ", 经度("+lon + "), 纬度(" + lat + ")");
	
	var mk = new BMap.Marker(new BMap.Point(changePos.uLon, changePos.uLat));
	mk.setTitle("您选择的当前位置");
	gMap.clearOverlays();
	gMap.addOverlay(mk);
}
function food() {
	changeSelState("idSelModeFD");
}
function film() {
	changeSelState("idSelModeDY");
}
function normal() {
	changeSelState("idSelModeNormal");
}
function changeSelState(now) {
	if (now == lastSelModeId) {
		return;
	}
	
	var cSelModeUsed = "cSelModeUsed";
	$("#"+lastSelModeId).removeClass(cSelModeUsed);
	$("#"+now).addClass(cSelModeUsed);
	
	lastSelModeId = now;
	
	refreshTg(true);
}
function displayUserPos() {
	$("#idPos").empty();
	if (posDesc != "") {
		$("#idPos").append(posDesc);
	} else {
		$("#idPos").append("未设置(点击旁边链接更改)");
	}
}
function selPosOK() {
	uLon = changePos.uLon*1000000;
	uLat = changePos.uLat*1000000;
	posDesc = changePos.posDesc;
	$("#idChangePos").css("display","none");
	displayUserPos();
	refreshTg(true);
	storeUserPos();
}
function selPosCancel(){
	$("#idChangePos").css("display","none");
}
function toTop() {
	window.scrollTo(0,0);
}

//global code
loadRecent();
loadUserPos();

displayUserPos();
refreshPos();
refreshTg(true);

var elm = document.getElementsByTagName("body")[0];
elm.addEventListener("scroll",scrollEvent, false);

window.onscroll = scrollEvent;

$("#idRet2Top").click(toTop);
$("#idClearRecent").click(clearRecent);
$("#idSelModeFD").click(food);
$("#idSelModeDY").click(film);
$("#idSelModeNormal").click(normal);
$("#idSelPosOK").click(selPosOK);
$("#idSelPosCancel").click(selPosCancel);

};
