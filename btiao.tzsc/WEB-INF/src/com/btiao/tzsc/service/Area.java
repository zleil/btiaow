package com.btiao.tzsc.service;

import java.io.Serializable;

public class Area implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4474512948179651499L;
	
	public long id;
	public String desc;
	
	public String toString() {
		return "{id:"+id+",desc:\""+desc+"\"}";
	}
}
