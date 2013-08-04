package com.btiao.user.restlet;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.user.domain.BTiaoUser;
import com.btiao.user.domain.BTiaoUserLogInfo;
import com.btiao.user.service.UserMgr;

public class ResBTiaoUserLogInfo extends ResBTBase {
	/**
	 * when login, the loginId which is contained in uri must be this.
	 */
	private String reserved_loginId = "0";
	
	protected void pre() {
		uIdFromUrl = this.getAttribute("userId");
		loginId = this.getAttribute("loginId");
	}

	/**
	 * query login state, feature of administrator<br>
	 * TODO item. 
	 */
	@Override
	protected Object get(Form form) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * login operation.<br>
	 * url: .../users/{userId}/{loginId}, loginId must equals to 0.<br>
	 * @param arg contain passwd infomation.<br>
	 *        the type is BTiaoUser.<br>
	 *        valid attributes contain: id,passwd,authType
	 * @return user login token.<br>
	 *         the type is BTiaoUserLogInfo.<br>
	 *         valid attributes: token
	 */
	@Override
	protected Object put(Object arg) throws BTiaoExp {
		BTiaoUser u = (BTiaoUser)arg;
		
		if (!u.id.equals(uIdFromUrl) &&
			!loginId.equals(reserved_loginId)) {
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}
		
		String token = UserMgr.instance().login(u.id, u.passwd, u.authType);
		
		BTiaoUserLogInfo r = new BTiaoUserLogInfo();
		return r;
	}

	/**
	 * not to be implemented!
	 */
	@Override
	protected Object post(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * logout.
	 * @param arg content userid and token.
	 * @return always be null
	 */
	@Override
	protected Object del(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	private String uIdFromUrl;
	private String loginId;
}
