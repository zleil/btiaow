package com.btiao.product.restlet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.btiao.base.oif.restlet.RestFilterBasicAuth;


public class App extends Application {
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/positions/{positionId}", ResBTiaoPosition.class);
		router.attach("/positions/{positionId}/infos/{infoId}", ResBTiaoBlockInfo.class);
		router.attach("/positions/{positionId}/orders/{orderId}", ResBTiaoOrder.class);
		router.attach("/positions/{positionId}/infos", ResBTiaoBlockInfoGetFunc.class);
		router.attach("/positions/{positionId}/orders", ResBTiaoOrderGetAll.class);
		
		RestFilterBasicAuth authFilter = new RestFilterBasicAuth();
		authFilter.setNext(router);
		return authFilter;
	}
}

