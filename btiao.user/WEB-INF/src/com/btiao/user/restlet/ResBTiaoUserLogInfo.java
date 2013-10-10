package com.btiao.user.restlet;

import java.util.Collection;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.JsonCvtInfo;
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
		token = this.getAttribute("token");
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
	 * url: .../users/{userId}/auth/{token}, loginId must equals to 0.<br>
	 * @param arg contain authorization information.<br>
	 *        the type is BTiaoUser.<br>
	 *        valid attributes contain: id,passwd,authType
	 * @return user login token.<br>
	 *         the type is BTiaoUserLogInfo.<br>
	 *         valid attributes: token
	 */
	@Override
	@JsonCvtInfo(objClassName="com.btiao.user.domain.BTiaoUser")
	protected Object put(Object arg) throws BTiaoExp {
		BTiaoUser u = (BTiaoUser)arg;
		
		if (!u.id.equals(uIdFromUrl) ||
			!token.equals(reserved_loginId)) {
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}
		
		String token = UserMgr.instance().login(u.id, u.passwd, u.authType);
		
		BTiaoUserLogInfo r = new BTiaoUserLogInfo("", token);
		return r;
	}

	/**
	 * not to be implemented!
	 */
	@Override
	protected Object post(Object arg, Collection<String> attrList) throws BTiaoExp {
		return null;
	}

	/**
	 * logout.
	 * @param arg content user-id and token.
	 *        the type is BTiaoUserLogInfo.<br>
	 *        valid attributes contain: uId,token
	 * @return if it return, then always return null
	 * @throws when error arises, must throw a BTiaoExp with an error code.<br>
	 */
	@Override
	@JsonCvtInfo(objClassName="com.btiao.user.domain.BTiaoUserLogInfo")
	protected Object del(Object arg) throws BTiaoExp {
		BTiaoUserLogInfo info = (BTiaoUserLogInfo)arg;
		
		if (!uIdFromUrl.equals(info.uId)) {
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}
		
		UserMgr.instance().logout(info.uId, info.token);
		return null;
	}

	private String uIdFromUrl;
	private String token;
}
