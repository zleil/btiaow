package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.btiao.base.model.BTiaoRoot;
import com.btiao.base.oif.restlet.RestFilterBasicAuth;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.BlockInfo;
import com.btiao.product.domain.Order;
import com.btiao.product.domain.Position;
import com.btiao.product.restlet.RestBTiaoGetAll.Def;


public class App extends Application {
	public Restlet createInboundRoot() {
		router.attach("/positions/{positionId}", ResBTiaoPosition.class);
		router.attach("/positions/{positionId}/infos/{infoId}", ResBTiaoBlockInfo.class);
		router.attach("/positions/{positionId}/orders/{orderId}", ResBTiaoOrder.class);
		router.attach("/getId/{idName}", ResBTiaoGetId.class);
		router.attach("/usrInfoExt/{usrId}", ResBTiaoUsrInfoExt.class);
		
		//router.attach("/positions", ResBTiaoPositionGetAll.class);
		//router.attach("/positions/{positionId}/infos", ResBTiaoBlockInfoGetFunc.class);
		//router.attach("/positions/{positionId}/orders", RestBTiaoGetAll.class);
		
		def("/positions",BTiaoRoot.class,Position.class,RelName.posOfRoot,RelName.timeSeq,ids(),ids("lastId"));
		def("/positions/{posId}/orders",Position.class,Order.class,RelName.order,RelName.timeSeq,ids("posId"),ids("lastId"));
		def("/positions/{posId}/infos",Position.class,BlockInfo.class,RelName.blockInfo,RelName.timeSeq,ids("posId"),ids("lastId"));
		
		RestFilterBasicAuth authFilter = new RestFilterBasicAuth();
		authFilter.setNext(router);
		return authFilter;
	}
	
	private List<String> ids(String ... args) {
		List<String> r = new ArrayList<String>();
		for (String arg : args) {
			r.add(arg);
		}
		return r;
	}
	private void def(String url, 
			Class<?extends InfoMObject> parentClass,
			Class<?extends InfoMObject> objClass,
			String rightRelName, String downRelName,
			List<String> pidStrs, List<String> idStrs) {
		router.attach(url, RestBTiaoGetAll.class);
		RestBTiaoGetAll.def(new Def(url,parentClass,objClass,rightRelName,downRelName,pidStrs,idStrs));
	}
	
	private Router router = new Router(getContext());
}

