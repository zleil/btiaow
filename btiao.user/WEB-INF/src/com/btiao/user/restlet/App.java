package com.btiao.user.restlet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.btiao.base.oif.restlet.RestFilterBasicAuth;
import com.btiao.base.service.BTiaoServiceMgr;
import com.btiao.base.service.UserService;
import com.btiao.user.service.UserMgr;

public class App extends Application {
	public Restlet createInboundRoot() {
		BTiaoServiceMgr.instance().regService(UserService.class.getName(), 
				UserMgr.instance());
		
		Router router = new Router(getContext());
		router.attach("/users/{userId}", ResBTiaoUser.class);
		router.attach("/users/{userId}/auth/{token}", ResBTiaoUserLogInfo.class);
		
		RestFilterBasicAuth authFilter = new RestFilterBasicAuth();
		authFilter.setNext(router);
		return authFilter;
	}
}
