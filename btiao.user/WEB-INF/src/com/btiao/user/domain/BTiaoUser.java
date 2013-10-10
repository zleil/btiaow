package com.btiao.user.domain;

import com.btiao.infomodel.InfoMObjAttrDesc;
import com.btiao.infomodel.InfoMObject;

public class BTiaoUser extends InfoMObject {
	public BTiaoUser(String id) {
		this.id = id;
	}
	
	public BTiaoUser() {}
	
	@InfoMObjAttrDesc(key=true)
	public String id;
	
	public String nick;
	
	public String passwd;
	
	public int authType; //当前仅能取0
}
