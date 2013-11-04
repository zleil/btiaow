package com.btiao.base.model;

import java.util.List;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class BTiaoUser extends InfoMObject {
	public BTiaoUser(String id) {
		this.id = id;
	}
	
	public BTiaoUser() {}
	
	@Override
	public boolean initId(List<String> urlIds) {
		this.id = urlIds.get(0);
		return true;
	}
	
	@InfoMObjAttrDesc(key=true)
	public String id  = "";
	
	public String nick = "";
	
	public String passwd = "";
	
	public int authType; //当前仅支持0，即用户名/密码认证
}
