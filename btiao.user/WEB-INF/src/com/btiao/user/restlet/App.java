package com.btiao.user.restlet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class App extends Application {
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		router.attach("/{userId}", ResBTiaoUser.class);
		return router;
	}
}
