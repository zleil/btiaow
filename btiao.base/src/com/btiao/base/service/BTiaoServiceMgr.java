package com.btiao.base.service;

import java.util.HashMap;
import java.util.Map;

public class BTiaoServiceMgr {
	static public synchronized BTiaoServiceMgr instance() {
		if (inst == null) {
			inst = new BTiaoServiceMgr();
		}
		return inst;
	}
	
	static private BTiaoServiceMgr inst = null;
	
	/**
	 * get a service by class name.
	 * @param serviceName the interface name of the service.
	 * @return service object. <br>
	 *         if there have no specified service registered,<br> 
	 *         then, it return null.<br>
	 */
	public synchronized Object getService(String serviceName) {
		Object svc = svcs.get(serviceName);
		return svc;
	}
	
	/**
	 * register a service.<br>
	 * @param serviceName the interface name of the service.<br>
	 * @param svc service name.<br>
	 *        if it is null, then the specified service will be unregistered, <br>
	 *        and if the service have not register before, then nothing will<br> 
	 *        be done.<br>
	 */
	public synchronized void regService(String serviceName, Object svc) {
		if (svc == null) {
			svcs.remove(serviceName);
			return;
		}
		
		svcs.put(serviceName, svc);
	}
	
	private BTiaoServiceMgr() {}
		
	private Map<String,Object> svcs = new HashMap<String,Object>();
}
