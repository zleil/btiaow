package com.btiao.base.service;

public interface UserService {
	/**
	 * basic token checking.<br>
	 * just check whether the token of user is valid.<br>
	 * @param uId user identity.
	 * @param token token string, see user management feature.
	 * @return
	 */
	boolean baseAuth(String uId, String token);
}
