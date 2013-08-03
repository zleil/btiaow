package com.btiao.user.restlet;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.user.domain.BTiaoUser;

public class ResBTiaoUser extends ResBTBase {

	@Override
	protected Object get(Form form) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object put(Object arg) throws BTiaoExp {
		BTiaoUser u = (BTiaoUser)arg;
		if (!u.id.equals(this.getContext().getAttributes().get("userId"))){
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}
		
		return null;
	}

	@Override
	protected Object post(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

}
