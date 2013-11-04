package com.btiao.user.restlet;

import java.util.Collection;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.model.BTiaoUser;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.user.service.UserMgr;

public class ResBTiaoUser extends ResBTBase {
	@Override
	protected void pre() {
		uIdFromUrl = (String)this.getAttribute("userId");
	}

	@Override
	protected Object get(Form form) throws BTiaoExp {
		return UserMgr.instance().getUser(uIdFromUrl);
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.base.model.BTiaoUser")
	protected Object put(Object arg) throws BTiaoExp {
		BTiaoUser u = (BTiaoUser)arg;
		if (!uIdFromUrl.equals(OBJID_WHEN_CREATE)){
			String errMsg = "lastId in url is not '"+OBJID_WHEN_CREATE+"' while creating object";
			throw new BTiaoExp(ErrCode.WRONG_PARAM, errMsg);
		}

		UserMgr.instance().addUser(u);
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.base.model.BTiaoUser")
	protected Object post(Object arg, Collection<String> attrs) throws BTiaoExp {
		BTiaoUser u = (BTiaoUser)arg;
		if (!u.id.equals(uIdFromUrl)){
			String errMsg = "while modifying object, the id in url does not equal to id in json object";
			throw new BTiaoExp(ErrCode.WRONG_PARAM, errMsg);
		}
		
		UserMgr.instance().updateUser(u, attrs);
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		UserMgr.instance().delUser(uIdFromUrl);
		return null;
	}

	private String uIdFromUrl;
}
