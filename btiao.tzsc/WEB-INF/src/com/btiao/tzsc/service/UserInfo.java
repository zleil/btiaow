package com.btiao.tzsc.service;

import java.io.Serializable;

public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1142200470491572961L;
	
	public String usrId;
	public String nick;
	
	public String telId;
	public String homeId;
	
	public String realSalePosId;
	
	public boolean wuyeAuth = false; //物业认证
	public boolean doorNoPicAuth = false; //门牌图片认证
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("usrId:\""+usrId+"\",");
		sb.append("nick:\""+nick+"\"");
		sb.append("telId:\""+telId+"\"");
		sb.append("homeId:\""+homeId+"\"");
		sb.append("realSalePosId:\""+realSalePosId+"\"");
		sb.append("wuyeAuth:\""+wuyeAuth+"\"");
		sb.append("doorNoPicAuth:\""+doorNoPicAuth+"\"");
		sb.append("}");
		
		return sb.toString();
	}
}
