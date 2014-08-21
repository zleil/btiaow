package com.btiao.tzsc.service;

import java.io.Serializable;

/**
 * 用户的信息
 * @author zleil
 *
 */
public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1142200470491572961L;
	
	public final String usrId;
	public String nick;
	
	public String telId;
	public String homeId;
	
	public long dengjiTime;
	public long dengjiApproveTime;
	
	public String realSalePosId;
	
	public boolean wuyeAuth = false; //物业认证
	public long wuyeAuthTime; //物业认证通过时间
	
	public boolean doorNumPicAuth = false; //门牌图片认证
	public long doorNumPicAuthTime; //门牌图片认证通过时间
	
	public UserInfo(String usrId) {
		this.usrId = usrId;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"usrId\":\""+usrId+"\",");
		sb.append("\"nick\":\""+nick+"\",");
		sb.append("\"telId\":\""+telId+"\",");
		sb.append("\"homeId\":\""+homeId+"\",");
		sb.append("\"dengjiTime\":"+dengjiTime+",");
		sb.append("\"dengjiApproveTime\":"+dengjiApproveTime+",");
		sb.append("\"realSalePosId\":\""+realSalePosId+"\",");
		sb.append("\"wuyeAuth\":\""+wuyeAuth+"\",");
		sb.append("\"wuyeAuthTime\":"+wuyeAuthTime+",");
		sb.append("\"doorNumPicAuth\":"+doorNumPicAuth+",");
		sb.append("\"doorNumPicAuthTime\":"+doorNumPicAuthTime);
		sb.append("}");
		
		return sb.toString();
	}
}
