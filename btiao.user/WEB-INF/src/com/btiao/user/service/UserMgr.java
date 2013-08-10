package com.btiao.user.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.service.UserService;
import com.btiao.base.utils.BTiaoLog;
import com.btiao.infomodel.InfoMBaseService;
import com.btiao.infomodel.InfoMService;
import com.btiao.infomodel.RelType;
import com.btiao.user.domain.BTiaoUser;
import com.btiao.user.domain.BTiaoUserLogInfo;

public class UserMgr implements UserService {
	static public void main(String[] args) {
		try {
			assert(false);
			System.out.println("please use VM argument '-ea'.");
		} catch (Exception e) {
			System.out.println("success!");
		}
	}
	
	static public synchronized UserMgr instance() {
		if (inst == null) {
			inst = new UserMgr();
		}
		return inst;
	}
	
	static private UserMgr inst;
	
	/**
	 * one user can login 5 times.
	 */
	static private int MAX_LOGIN_NUM_PER_USER = 5;
	
	/**
	 * time of login.<br>
	 * if there is no operatotion from this login-token, <br>
	 * then the token will be invalid.<br>
	 */
	//TODO to be implement.
	static private long TIMEOUT = 60*10;
	
	/**
	 * super user identiy.<br>
	 */
	static private String ROOT_USER_ID = "_mgr0";
	
	/**
	 * the plain password, which haven't been hashed.
	 */
	static private String ROOT_USER_PASSWD = "zleil";
	
	/**
	 * login.
	 * @param uId user identity
	 * @return token, used to authentication or logout
	 */
	public synchronized String login(String uId, String passwd, int authType)
	throws BTiaoExp {
		if (!auth(uId, passwd, authType)) {
			throw new BTiaoExp(ErrCode.AUTH_FAILED, null);
		}
		
		BTiaoUser u = new BTiaoUser(uId);

		//TODO this can't really constrain the max login number.
		//we should implement this constrain in the info model.
		Collection<?> infos = 
				InfoMService.instance().getRelObject(u, getUserLoginRel(), BTiaoUserLogInfo.class);
		if (infos.size() >= MAX_LOGIN_NUM_PER_USER) {
			throw new BTiaoExp(ErrCode.REACHED_MAX_LOGIN_PER_USER, null);
		}
		
		String token = genToken();
		BTiaoUserLogInfo info = new BTiaoUserLogInfo(uId, token);
		info.startTime = System.currentTimeMillis();
		info.endTime = 0;
		
		InfoMBaseService.instance().add(info);
		InfoMBaseService.instance().addRel(u, info, getUserLoginRel());
		return info.token;
	}
	
	public synchronized void logout(String uId, String token) throws BTiaoExp {
		BTiaoUser u = new BTiaoUser(uId);
		
		BTiaoUserLogInfo info = new BTiaoUserLogInfo(uId, token);
		
		InfoMService.instance().delBFromeA(u, info, getUserLoginRel());
	}
	
	public synchronized void addUser(BTiaoUser u) throws BTiaoExp {
		if (!isCreateUserInfoComplete(u)){
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}
		
		InfoMBaseService.instance().add(u);
	}
	
	public synchronized void delUser(String uId) throws BTiaoExp {
		BTiaoUser u = new BTiaoUser(uId);
		InfoMBaseService.instance().del(u);
	}
	
	public synchronized void updateUser(BTiaoUser u) throws BTiaoExp {
		InfoMBaseService.instance().mdf(u);
	}
	
	public synchronized BTiaoUser getUser(String uId) throws BTiaoExp {
		BTiaoUser u = new BTiaoUser(uId);
		InfoMBaseService.instance().get(u);
		return u;
	}
	
	@Override
	public boolean baseAuth(String uId, String token) {
		BTiaoUserLogInfo info = new BTiaoUserLogInfo(uId, token);
		try {
			InfoMBaseService.instance().get(info);
		} catch (BTiaoExp e) {
			return false;
		}
		
		return true;
	}
	
	public boolean auth(String uId, String passwd, int authType) {
		BTiaoUser u = new BTiaoUser(uId);
		try {
			InfoMBaseService.instance().get(u);
		} catch (BTiaoExp e) {
			return false;
		}
		
		if (authType != 0) {
			return false; //当前仅支持为0的认证
		}
		
		if (!u.passwd.equals(passwd)) {
			return false;
		}
		
		return true;
	}
	
	private UserMgr() {
		addRootUser();
	}
	
	private RelType getUserLoginRel() {
		return new RelType("login");
	}
	
	private void addRootUser() {
		BTiaoUser u = new BTiaoUser(ROOT_USER_ID);
		u.nick = "super user";
		u.passwd = passwdHash(ROOT_USER_PASSWD);

		try {
			InfoMBaseService.instance().add(u);
		} catch (BTiaoExp e) {
			
		}
	}
	
	private String genToken() {
		//TODO 很容易枚举攻击，要考虑对登录限流，并加大token长度
		return ""+random.nextLong();
	}
	
	private String passwdHash(String passwd) {
		//TODO to implement, must be the same with client.
		return passwd;
	}
	
	private boolean isCreateUserInfoComplete(BTiaoUser u) {
		if (!(u.authType == 0 && u.id != null && u.nick != null &&
			u.passwd != null && !u.id.equals("") && !u.passwd.equals(""))) {
			return false;
		}
		
		return !isReservedUID(u.id);
	}
	
	private boolean isReservedUID(String id) {
		return id.startsWith("_mgr");
	}
	
	private SecureRandom random = new SecureRandom();
	
	Logger log = BTiaoLog.get();
}
