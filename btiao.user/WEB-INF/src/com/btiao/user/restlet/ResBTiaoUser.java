package com.btiao.user.restlet;

import java.util.Collection;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.user.domain.BTiaoUser;
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
	@JsonCvtInfo(objClassName="com.btiao.user.domain.BTiaoUser")
	protected Object put(Object arg) throws BTiaoExp {
		BTiaoUser u = (BTiaoUser)arg;
		if (!uIdFromUrl.equals(OBJID_WHEN_CREATE)){
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}

		UserMgr.instance().addUser(u);
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.user.domain.BTiaoUser")
	protected Object post(Object arg, Collection<String> attrs) throws BTiaoExp {
		BTiaoUser u = (BTiaoUser)arg;
		if (!u.id.equals(uIdFromUrl)){
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
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
