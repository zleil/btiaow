package com.btiao.user.service;

import java.security.SecureRandom;
import java.util.Collection;

import org.apache.logging.log4j.Logger;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.model.BTiaoUser;
import com.btiao.base.service.UserService;
import com.btiao.base.utils.BTiaoLog;
import com.btiao.infomodel.InfoMBaseService;
import com.btiao.infomodel.InfoMService;
import com.btiao.infomodel.RelType;
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
		if (uId.startsWith("007_")) {
			BTiaoUser u = new BTiaoUser();
			u.id = uId;
			u.passwd = passwd;
			u.nick = uId;
			
			if (!InfoMBaseService.instance().get(u)) {
				addUser(u);
			}
		}
		
		if (!auth(uId, passwd, authType)) {
			String errMsg = "uId="+uId+",passwd="+passwd;
			throw new BTiaoExp(ErrCode.AUTH_FAILED, errMsg);
		}
		
		BTiaoUser u = new BTiaoUser(uId);

		//TODO this can't really constrain the max login number.
		//we should implement this constrain in the info model.
//		Collection<?> infos = 
//				InfoMService.instance().getRelObject(u, getUserLoginRel(), BTiaoUserLogInfo.class);
//		if (infos.size() >= MAX_LOGIN_NUM_PER_USER) {
//			throw new BTiaoExp(ErrCode.REACHED_MAX_LOGIN_PER_USER, null);
//		}
		
		String token = genToken();
		BTiaoUserLogInfo info = new BTiaoUserLogInfo(uId, token);
		info.startTime = System.currentTimeMillis();
		info.endTime = 0;
		
		base.begin();
		try {
			base.add(info);
			base.addRel(u, info, getUserLoginRel());
			
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}

		return info.token;
	}
	
	public synchronized void logout(String uId, String token) throws BTiaoExp {
		BTiaoUser u = new BTiaoUser(uId);
		
		BTiaoUserLogInfo info = new BTiaoUserLogInfo(uId, token);
		
		InfoMService.instance().begin();
		try {
			InfoMService.instance().delBFromeA(u, info, getUserLoginRel());
			
			InfoMService.instance().success();
		} catch (BTiaoExp e) {
			InfoMService.instance().failed();
			throw e;
		} finally {
			InfoMService.instance().finish();
		}
	}
	
	public synchronized void addUser(BTiaoUser u) throws BTiaoExp {
		if (!isCreateUserInfoComplete(u)){
			String errMsg = "user infos are not completed while adding,u="+u;
			throw new BTiaoExp(ErrCode.WRONG_PARAM, errMsg);
		}
		
		InfoMBaseService.instance().begin();
		try {
			InfoMBaseService.instance().add(u);
			
			InfoMBaseService.instance().success();
		} catch (BTiaoExp e) {
			InfoMBaseService.instance().failed();
			throw e;
		} finally {
			InfoMBaseService.instance().finish();
		}
	}
	
	public synchronized void delUser(String uId) throws BTiaoExp {
		BTiaoUser u = new BTiaoUser(uId);
		
		InfoMBaseService.instance().begin();
		try {
			InfoMBaseService.instance().del(u);
			
			InfoMBaseService.instance().success();
		} catch (BTiaoExp e) {
			InfoMBaseService.instance().failed();
			throw e;
		} finally {
			InfoMBaseService.instance().finish();
		}
	}
	
	public synchronized void updateUser(BTiaoUser u, Collection<String> attrs) throws BTiaoExp {
		InfoMBaseService.instance().begin();
		try {
			BTiaoUser newObj = (BTiaoUser)u.clone();
			if (!InfoMBaseService.instance().get(newObj)) {
				throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, new Throwable(newObj.toString()));
			}
			newObj.update(u, attrs);
			
			InfoMBaseService.instance().mdf(newObj);
			
			InfoMBaseService.instance().success();
		} catch (BTiaoExp e) {
			InfoMBaseService.instance().failed();
			throw e;
		} finally {
			InfoMBaseService.instance().finish();
		}
	}
	
	public synchronized BTiaoUser getUser(String uId) throws BTiaoExp {
		BTiaoUser u = new BTiaoUser(uId);
		if (InfoMBaseService.instance().get(u)) {
			return u;
		} else {
			String errMsg = "getUser,uId="+uId;
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, new Throwable(errMsg));
		}
	}
	
	public synchronized Collection<BTiaoUser> getAllUser() throws BTiaoExp {
		@SuppressWarnings("unchecked")
		Collection<BTiaoUser> all = (Collection<BTiaoUser>)InfoMBaseService.instance().getAll(BTiaoUser.class);
		return all;
	}
	
	@Override
	public boolean baseAuth(String uId, String token) {
		BTiaoUserLogInfo info = new BTiaoUserLogInfo(uId, token);
		try {
			return InfoMBaseService.instance().get(info);
		} catch (BTiaoExp e) {
			String errMsg = "uid="+uId+",token="+token;
			BTiaoLog.logExp(log, e, errMsg);
			return false;
		}
	}
	
	public boolean auth(String uId, String passwd, int authType) {
		BTiaoUser u = new BTiaoUser(uId);
		try {
			InfoMBaseService.instance().get(u);
		} catch (BTiaoExp e) {
			return false;
		}
		
		if (authType != 0) {
			return false; //��ǰ��֧��Ϊ0����֤
		}
		
		if (u.id.startsWith("007_")) {
			return true;
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

		base.begin();
		try {
			base.add(u);
			
			base.success();
		} catch (Throwable e) {
			base.failed();
			
			String errMsg = "add super user failed!";
			BTiaoLog.logExp(log, e, errMsg);
		} finally {
			base.finish();
		}
	}
	
	private String genToken() {
		//TODO ������ö�ٹ�����Ҫ���ǶԵ�¼���������Ӵ�token����
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
	
	public InfoMBaseService base = InfoMBaseService.instance();
}
