var gMap;
var tgRoot = "/btiao/tgGen"

function onLoad() {
	$("#p1x").blur(function() {
		p1.x = $("#p1x").val();
	});
	$("#p1y").blur(function() {
		p1.y = $("#p1y").val();
	});
	$("#p2x").blur(function() {
		p2.x = $("#p2x").val();
	});
	$("#p2y").blur(function() {
		p2.y = $("#p2y").val();
	});
	p1.x = 116.414;$("#p1x").val(p1.x);
	p1.y = 39.915;$("#p1y").val(p1.y);
	p2.x = p1.x;$("#p2x").val(p2.x);
	p2.y = p1.y;$("#p2y").val(p2.y);
	next_p2();

	gMap = new BMap.Map("out");
	gMap.centerAndZoom(new BMap.Point(116.414, 39.915), 15);
	gMap.enableKeyboard();
	gMap.clearHotspots();
	
	gMap.addEventListener("click", function(e){    
		 $("#txtoutdetail").append("<p>lng="+e.point.lng + ", lat=" + e.point.lat+"</p>");
		 return false;
	});
}
function drv(p1, p2) {
	var driving = new BMap.DrivingRoute(gMap, {
		renderOptions: { map: gMap, autoViewport: true }
	});
	
	//driving.search("");
	driving.search(new BMap.Point(p1.x, p1.y), new BMap.Point(p2.x, p2.y));
}
function printGeo(lon, lat, title) {
	var myGeo = new BMap.Geocoder();
	myGeo.getLocation(new BMap.Point(lon, lat), function(result){
		if (result){ 
			$(txtout).empty();
			$(txtout).append("lon="+lon+",lat="+lat+","+result.address);
		}
	});
	
	gMap.panTo(new BMap.Point(lon, lat), 15);
	var mk = new BMap.Marker(new BMap.Point(lon, lat));
	mk.setTitle(title);
	gMap.addOverlay(mk);
}

var cos40 = 0.766;
var lonDist = cos40*2*3.1415926*6371.004*1000/360; //alert(lonDist); 
var latDist = 2*3.1415926*6356.755*1000/360; //alert(latDist);

function judgeNm(p, dx, dy) {
	//var lonDist = 111194.9946; //2*3.1415926*6371.004*1000/360; //11194.9946 m
	//var latDist = 110946.3026; //2*3.1415926*6356.755*1000/360; //110946.3026 m	
	var r = {x:(p.x*1000000+1000000*dx/lonDist)/1000000, y:(p.y*1000000+1000000*dy/latDist)/1000000};
	return r;
}
function next_p2() {
	p2 = judgeNm(p2, 500, 500);
	$("#p2x").val(p2.x);
	$("#p2y").val(p2.y);
}

var p1 = {x:116.414,y:39.915};
var p2 = judgeNm(p1, 1*1000, 1*1000);
var city = "beijing";

	var INTERVAL = 1000;
	var INTERVAL_NUM = 2500;
	var PER_NUM = 100;
	
function genAll(city, p, stepDist, pp, startNumX, endNumX) {
	printGeo(p.x, p.y, "start");
	printGeo(pp.x, pp.y, "end");
	
	p.x = parseInt(p.x*1000*1000);
	p.y = parseInt(p.y*1000*1000);
	
	pp.x = parseInt(pp.x*1000*1000);
	pp.y = parseInt(pp.y*1000*1000);
	
	var stepX = stepDist/lonDist;
	stepX = (parseInt(stepX*1000*1000))/(1000*1000); 
	stepX=parseInt(stepX*1000*1000);
	var stepY = stepDist/latDist; 
	stepY = (parseInt(stepY*1000*1000))/(1000*1000); 
	stepY=parseInt(stepY*1000*1000);
	
	var numX = (pp.x - p.x)/stepX; numX = parseInt(numX) + 1;
	var numY = (pp.y - p.y)/stepY; numY = parseInt(numY) + 1;
	
	if (!startNumX) {
		startNumX = 0;
	}
	if (!endNumX) {
		endNumX = numX;
	}
	
	city = city + "," + p.x + "," + p.y + "," + stepX + "," + stepY + "," + 
		numX + "," + numY + "," + startNumX + "," + endNumX;
	window.city = city;
	
	$("#txtoutdetail").append("stepX="+stepX+",stepY="+stepY+",numX="+numX+",numY="+numY+",startX="+startNumX+",endX="+endNumX+"<br/>");
	
	var pp2 = {x:(p.x + stepX*numX), y:(p.y + stepY*numY)};
	printGeo(pp2.x, pp2.y, "end2");
	
	var total = (numX*numY-1)*(endNumX-startNumX)*numY; //numX*numY*(numX*numY-1);
	
	var tm_begin = new Date().getTime();
	var tm_start = new Date().getTime();
	var tm_end = new Date().getTime();
	
	var iFirst=0;
	var jFirst=0;
	var iSecond=0;
	var jSecond=0;
	
	genAll.okGJNum = 0;
	genAll.okZJNum = 0;
	genAll.okDistOnlyNum = 0;
	genAll.okGJNumLast = 0;
	genAll.okZJNumLast = 0;
	genAll.okDistOnlyNumLast = 0;
	genAll.okGJBDNum = 0;
	genAll.okZJBDNum = 0;
	genAll.okDistOnlyBDNum = 0;
	
	initDB(city);
	
	iFirst = startNumX;
	
	var nextIJFirst = function () {
		if (iFirst >= endNumX) {
			return false;
		}
		
		++jFirst;
		if (jFirst>=numY) {
			jFirst = 0;
			++iFirst;
			
			if (iFirst>=endNumX) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}
	var canUseIJFirst = function() {
		if (iFirst < startNumX || iFirst >= endNumX || jFirst >= numY) {
			return false;
		} else {
			return true;
		}
	}
	var canUseIJSecond = function() {
		if (iSecond >= numX || jSecond >= numY) {
			return false;
		} else {
			return true;
		}
	}
	var nextIJSecond = function () {
		++jSecond;
		if (jSecond>=numY) {
			jSecond = 0;
			++iSecond;
			
			if (iSecond>=numX) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	setTimeout(function() {
		var exeNum = 0;
		
		var firstIsLeggal = true;
		while ((firstIsLeggal=canUseIJFirst()) && exeNum<INTERVAL_NUM) {
			p1 = {x:(p.x+iFirst*stepX), y:(p.y+jFirst*stepY)};
			p2 = {x:(p.x+iSecond*stepX), y:(p.y+jSecond*stepY)};
			
			if (!(p1.x == p2.x && p1.y == p2.y)) {
				//getDistZJ(city, p1, p2);
				//getDistGJ(city, p1, p2);
				getDistOnly(city, p1, p2);
				++exeNum;
			}
			
			//next p1 & p2
			secondIsLeggal = nextIJSecond();
			if (!secondIsLeggal) {
				iSecond = 0;
				jSecond = 0;
				
				firstIsLeggal = nextIJFirst();
			}
		}

		if (!firstIsLeggal) {
			flushcache();
		}
		
		tm_end = new Date().getTime();
		var time = (tm_end-tm_start)/1000;
		var useTime = (tm_end-tm_begin)/1000;

		var eclipseNumGJ = total - genAll.okGJNum;
		var eclipseNumZJ = total - genAll.okZJNum;
		var eclipseNumDist = total - genAll.okDistOnlyNum;
		
		var spGJ = (genAll.okGJNum-genAll.okGJNumLast)/time; spGJ = parseInt(spGJ);
		var spZJ = (genAll.okZJNum-genAll.okZJNumLast)/time; spZJ = parseInt(spZJ);
		var spDist = (genAll.okDistOnlyNum-genAll.okDistOnlyNumLast)/time; spDist = parseInt(spDist);
		
		var hGJ = (eclipseNumGJ)/(spGJ*3600); hGJ = parseInt(hGJ);
		var mGJ = ((eclipseNumGJ)/(spGJ*60))%60; mGJ = parseInt(mGJ);
		var sGJ = ((eclipseNumGJ)/spGJ)%60; sGJ = parseInt(sGJ);
		
		var hZJ = (eclipseNumZJ)/(spZJ*3600); hZJ = parseInt(hZJ);
		var mZJ = ((eclipseNumZJ)/(spZJ*60))%60; mZJ = parseInt(mZJ);
		var sZJ = ((eclipseNumZJ)/spZJ)%60; sZJ = parseInt(sZJ);
		
		var hDist = (eclipseNumDist)/(spDist*3600); hDist = parseInt(hDist);
		var mDist = ((eclipseNumDist)/(spDist*60))%60; mDist = parseInt(mDist);
		var sDist = ((eclipseNumDist)/spDist)%60; sDist = parseInt(sDist);
		
		$("#genAllProcess").empty();
		$("#genAllProcess").append("<p>total: "+total+",总共耗费： "+parseInt(useTime/3600)+"小时,"+parseInt(useTime/60)%60+"分钟,"+parseInt(useTime)%60+"秒"+"</p>");
		$("#genAllProcess").append("<p>finished_gj: "+genAll.okGJNum+","+genAll.okGJBDNum+",已入库_gj: "+spGJ+",总共耗费: "+hGJ+"灏忔椂,"+mGJ+"小时,"+sGJ+"秒"+"</p>");
		$("#genAllProcess").append("<p>finished_zj: "+genAll.okZJNum+","+genAll.okZJBDNum+",已入库_zj: "+spZJ+",总共耗费: "+hZJ+"灏忔椂,"+mZJ+"鍒嗛挓,"+sZJ+"秒"+"</p>");
		$("#genAllProcess").append("<p>finished_dist: "+genAll.okDistOnlyNum+","+genAll.okDistOnlyBDNum+",已入库_dist: "+spDist+",总共耗费: "+hDist+"小时,"+mDist+"分钟,"+sDist+"秒"+"</p>");
		
		if (genAll.okDistOnlyNumLast != genAll.okDistOnlyNum) {
			tm_start = new Date().getTime();
			genAll.okGJNumLast = genAll.okGJNum;
			genAll.okZJNumLast = genAll.okZJNum;
			genAll.okDistOnlyNumLast = genAll.okDistOnlyNum;
		}
		
		if (genAll.okDistOnlyNum == total) {
			$("#genAllProcess").append("<p>success!!!");
		} else {
			flushFailCache();
			setTimeout(arguments.callee, INTERVAL);
		}
	}, 0);
}
function push2cache(o) {
	if (!sendDist.cache) {
		sendDist.cache = [];
	}
	sendDist.cache.push(o);

	if (sendDist.cache.length >= PER_NUM) {
		flushcache();
	} else if (sendDist.cache.length >= PER_NUM/2) {
		flushFailCache();
	}
}
function flushFailCache() {
	if (!sendDist.blockCache) {
		return;
	}
	
	for (var i=0; i<sendDist.blockCache.length; ++i) {
		sendDist(sendDist.blockCache[i]);
	}
}
function flushcache() {
	if (!sendDist.cache) return;
	
	var cache = sendDist.cache;
	sendDist.cache = [];

	var start = 0;
	while (start<cache.length) {
		flushCacheBlock(cache, start, PER_NUM);
		
		start += PER_NUM;
	}
}
function zlWrapper(d) {
	this.d = d;
}
function flushCacheBlock(cache, start, num) {
	var p1x = cache[start].p1x;
	var p1y = cache[start].p1y;
	var p2x = cache[start].p2x;
	var p2y = cache[start].p2y;
	var city = cache[start].city;
	var dist = cache[start].dist;
	var dist_zj = cache[start].dist_zj ? cache[start].dist_zj : ".";
	var dist_gj = cache[start].dist_gj ? cache[start].dist_gj : ".";
	var time_gj = cache[start].time_gj ? cache[start].time_gj : ".";
	var time_zj = cache[start].time_zj ? cache[start].time_zj : ".";
	
	var end = start + num;
	var i=start+1;
	for (; i<end && i<cache.length; ++i) {
		p1x +="," + (cache[i].p1x ? cache[i].p1x : ".");
		p1y +="," + (cache[i].p1y ? cache[i].p1y : ".");
		p2x +="," + (cache[i].p2x ? cache[i].p2x : ".");
		p2y +="," + (cache[i].p2y ? cache[i].p2y : ".");
		dist_zj +="," + (cache[i].dist_zj ? cache[i].dist_zj : ".");
		dist_gj +="," + (cache[i].dist_gj ? cache[i].dist_gj : ".");
		time_gj +="," + (cache[i].time_gj ? cache[i].time_gj : ".");
		time_zj +="," + (cache[i].time_zj ? cache[i].time_zj : ".");
		dist +="," + (cache[i].dist ? cache[i].dist : ".");
	}
	
	var sendNum = i-start;
	if (sendNum > 0) {
		sendDist(new zlWrapper({city:city,p1x:p1x,p1y:p1y,p2x:p2x,p2y:p2y,
			dist_zj:dist_zj,dist_gj:dist_gj,
			time_zj:time_zj,time_gj:time_gj,
			dist:dist,multi:sendNum}));
	}
}
function sendDist(d) {
	var url = tgRoot + "/addp1p2dist";
	
	$.ajax({url:url, data:d.d, dataType:"text",success:function(result) {
		if (result == "true"){
			if (genAll.okDistOnlyNum) {
				genAll.okDistOnlyNum += d.d.multi;
			} else {
				genAll.okDistOnlyNum = d.d.multi;
			}
		} else {
			if (!sendDist.blockCache) {
				$("#txtoutdetail").append("error!");
				sendDist.blockCache = [];
			}
			sendDist.blockCache.push(d);
		}
		d = null;
	}});
}

function getDistOnly(city, p1, p2) {
	var lineDist = gMap.getDistance(new BMap.Point(p1.x/(1000*1000), p1.y/(1000*1000)),new BMap.Point(p2.x/(1000*1000), p2.y/(1000*1000)));
	lineDist = parseInt(lineDist);
	var time = 0;
	var totalDist = 0;
	
	var P1 = p1;
	var P2 = p2;
	
	//genAll.okDistOnlyNum = genAll.okDistOnlyNum ? (genAll.okDistOnlyNum + 1) : 1;
	//return;
	genAll.okDistOnlyBDNum = genAll.okDistOnlyBDNum ? (genAll.okDistOnlyBDNum + 1) : 1;
	push2cache({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist:lineDist});
}
function getDistZJ(city, p1, p2, disable) {
	//alert(p1.x);alert(p1.y);alert(p2.x);alert(p2.y);
	var options = {
		policy: BMAP_DRIVING_POLICY_LEAST_TIME,
		onSearchComplete: function(results) {
			var status = driving.getStatus();
			if (status == BMAP_STATUS_TIMEOUT) {
				failCB({p1:p1,p2:p2}, true, false, false);
			} else { 
				var totalDist = -1;//鍗曚綅锛氱背锛�1琛ㄧず鏈煡
				var lineDist = -1;//鍗曚綅锛氱背锛�1琛ㄧず鏈煡
				var time = -1;//鍗曚綅锛氬垎閽燂紝-1琛ㄧず鏈煡
				var P1 = p1;
				var P2 = p2;
	
				if (status == BMAP_STATUS_SUCCESS) {
					var plan = results.getPlan(0);
					if (0) { for (var rtIdx=0; rtIdx<plan.getNumRoutes(); ++rtIdx) {
						var route = plan.getRoute(0);
						var d = route.getDistance(false); //鍗曚綅锛氱背
						totalDist += d;
					}
					time = 60*totalDist/(50*1000); } //鎸夌収骞冲潎姣忓皬鏃�0鍏噷 
					
					totalDist = plan.getDistance(false);
					totalDist = parseInt(totalDist);
					time = plan.getDuration(false);
					time = parseInt(time);	
				} else {
					; //鏌ヨ閿欒锛岃〃绀烘湭鐭�
					$("#txtoutdetail").append("<p>zjerr="+status+",p1.x="+p1.x+",p1.y="+p1.y+",p2.x="+p2.x+",p2.y="+p2.y+"</p>");
				}
				
				lineDist = gMap.getDistance(new BMap.Point(p1.x/(1000*1000), p1.y/(1000*1000)),new BMap.Point(p2.x/(1000*1000), p2.y/(1000*1000)));
				lineDist = parseInt(lineDist);
				
				genAll.okZJBDNum = genAll.okZJBDNum ? (genAll.okZJBDNum + 1) : 1;
				
				push2cache({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_zj:totalDist, time_zj:time, dist:lineDist});
				/*
				sendDist({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_zj:totalDist, time_zj:time, dist:lineDist}, function (result) {
					if (result == "false") {
						failCB({p1:p1,p2:p2}, true, false, false);
					} else {
						genAll.okZJNum = genAll.okZJNum ? (genAll.okZJNum + 1) : 1;
					}
				});*/
			}
		}
		//,renderOptions: { map: gMap, autoViewport: true }
	};
	
	var driving = new BMap.DrivingRoute(gMap, options);
	
	driving.search(new BMap.Point(p1.x/(1000*1000), p1.y/(1000*1000)), new BMap.Point(p2.x, p2.y/(1000*1000)));
}
function getDistGJ(city, p1, p2, disable) {
	var P1 = p1;
	var P2 = p2;
	
	var transit = new BMap.TransitRoute(new BMap.Point(p1.x/(1000*1000), p1.y/(1000*1000)), {
		policy: BMAP_TRANSIT_POLICY_LEAST_TIME
	});
	transit.setSearchCompleteCallback(function(results){
		var status = transit.getStatus();
		if (status == BMAP_STATUS_TIMEOUT){
			failCB({p1:p1,p2:p2}, false, true, false);
		} else {
			var totalDistBx = -1; //鍗曚綅锛氱背锛�1琛ㄧず鏈煡
			var totalDistGj = -1; //鍗曚綅锛氱背锛�1琛ㄧず鏈煡
			var totalDist = -1; //鍗曚綅锛氱背锛�1琛ㄧず鏈煡
			var time = -1; //鍗曚綅锛氬垎閽燂紝-1琛ㄧず鏈煡
			var lineDist = -1; //鍗曚綅锛氱背锛�1琛ㄧず鏈煡
			
			if (status == BMAP_STATUS_SUCCESS) {
				var firstPlan = results.getPlan(0);
				
				if (0) { for (var i = 0; i < firstPlan.getNumRoutes(); i ++){
					var walk = firstPlan.getRoute(i);
					var d = walk.getDistance(false);
					totalDistBx += d;
					gMap.addOverlay(new BMap.Polyline(walk.getPath(), {lineColor: "green"}));
				}
				
				for (i = 0; i < firstPlan.getNumLines(); i ++){
					var line = firstPlan.getLine(i);
					gMap.addOverlay(new BMap.Polyline(line.getPath()));
					var d = line.getDistance(false);
					//var num = line.getNumViaStops(); //閫斿緞绔欑偣鏁扮洰
					totalDistGj += d;
				}
				time += 60*totalDistGj/(30*1000); //鍏氦閫熷害锛�0鍏噷/灏忔椂
				totalDist = totalDistGj + totalDistBx; }
				
				//$(txtoutdetail).append("totalDistGJ=" + totalDist+"("+lineDist+"), ");
			
				totalDist = firstPlan.getDistance(false);
				totalDist = parseInt(totalDist); //鍘绘帀灏忔暟浣�
				time = firstPlan.getDuration(false)/60;
				time = parseInt(time); //鍘绘帀灏忔暟浣�
			} else {
				; //鏌ヨ閿欒锛岃〃绀烘湭鐭�
				$("#txtoutdetail").append("<p>gjerr="+status+",p1.x="+p1.x+",p1.y="+p1.y+",p2.x="+p2.x+",p2.y="+p2.y+"</p>");
			}
				
			lineDist = gMap.getDistance(new BMap.Point(p1.x/(1000*1000), p1.y/(1000*1000)),new BMap.Point(p2.x/(1000*1000), p2.y/(1000*1000)));
			lineDist = parseInt(lineDist); //鍘绘帀灏忔暟浣�
			
			var P1 = cvtP(p1);
			var P2 = cvtP(p2);
			genAll.okZJBDNum = genAll.okZJBDNum ? (genAll.okZJBDNum + 1) : 1;
			push2cache({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_gj:totalDist, time_gj:time, dist:lineDist});
			/*
			sendDist({city:city,p1x:P1.x,p1y:P1.y,p2x:P2.x,p2y:P2.y,dist_gj:totalDist, time_gj:time, dist:lineDist}, function (result) {
				if (result == "false") {
					failCB({p1:p1,p2:p2}, false, true, false);
				} else {
					genAll.okGJNum = genAll.okGJNum ? (genAll.okGJNum + 1) : 1;
				}
			}); */
		}
	});
	
	transit.search(new BMap.Point(p1.x/(1000*1000), p1.y/(1000*1000)), new BMap.Point(p2.x/(1000*1000), p2.y/(1000*1000)));
}
function failCB(p2pObj, zj, gj, dist) {
	if (zj) {
		if (!genAll.failsZJ) genAll.failsZJ = [];
		
		genAll.failsZJ.push(p2pObj);
		$("#txtoutdetail").append("<p>zj,p1.x="+p2pObj.p1.x+",p1.y="+p2pObj.p1.y+",p2.x="+p2pObj.p2.x+",p2.y="+p2pObj.p2.y+"</p>");
	}
	if (gj) {
		if (!genAll.failsGJ) genAll.failsGJ = [];
		
		genAll.failsGJ.push(p2pObj);
		$("#txtoutdetail").append("<p>gj,p1.x="+p2pObj.p1.x+",p1.y="+p2pObj.p1.y+",p2.x="+p2pObj.p2.x+",p2.y="+p2pObj.p2.y+"</p>");
	}
	if (dist) {
		if (!genAll.failsDist) genAll.failsDist = [];
		
		genAll.failsDist.push(p2pObj);
		$("#txtoutdetail").append("<p>dist,p1.x="+p2pObj.p1.x+",p1.y="+p2pObj.p1.y+",p2.x="+p2pObj.p2.x+",p2.y="+p2pObj.p2.y+"</p>");
	}
}
function initDB(city) {
	var url = tgRoot + "/dbop";
	$.ajax({url:url,
			dataType:"text",
			async:false,
			data:{city:city,op:"init"},
			success:function (data, txtStatus) {
				if (data == "false") {
					$(txtout).append("<p>initDB.result=false</p>");
				} else {
					$(txtout).append("<p>initDB.result=true</p>");
				}
			}});
}
function closeDB(city) {
	var url = tgRoot + "/dbop";
	$.ajax({url:url,
			dataType:"text",
			async:false,
			data:{city:city,op:"close"},
			success:function (data, txtStatus) {
				if (data == "false") {
					$(txtout).append("<p>closeDB.result=false</p>");
				} else {
					$(txtout).append("<p>closeDB.result=true</p>");
				}
			}});
}
function clearInc() {
	var url = tgRoot + "/addp1p2dist";
	$.ajax({url:url,
			dataType:"text",
			async:false,
			data:{clearInc:"true"},
			success:function (data, txtStatus) {
				$(txtoutdetail).append("<p>clearInc success.</p>");
			}});
}




