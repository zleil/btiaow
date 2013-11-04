package com.btiao.user.restlet;

import java.util.Collection;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.model.BTiaoUser;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.user.service.UserMgr;

public class ResBTiaoAllUser extends ResBTBase {

	@Override
	protected Object get(Form form) throws BTiaoExp {
		Collection<BTiaoUser> us = UserMgr.instance().getAllUser();
		return us;
	}

	@Override
	protected Object put(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object post(Object arg, Collection<String> attrList)
			throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

}
