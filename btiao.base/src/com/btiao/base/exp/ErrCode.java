package com.btiao.base.exp;

public class ErrCode {
	static public final int SUCCESS = 0;
	static public final int WRONG_PARAM = 1;
	static public final int UNKOWN_ERR = 2;
	static public final int FUNC_NOT_SURPPORT = 3;
	static public final int AUTH_FAILED = 4;
	static public final int REACHED_MAX_LOGIN_PER_USER = 5;
	static public final int MAY_LOGOUTED = 6;
	
	static public final int ADD_DUP_OBJ_TO_INFO_MODEL = 100;
	static public final int ADD_DUP_REL_TO_INFO_MODEL = 100;
	static public final int OBJ_NOT_IN_INFO_MODEL = 101;
	
	static public final int INTERNEL_ERROR = 1000;
	static public final int TIME_OUT = 1001;
	static public final int USER_HAVE_NO_RIGHT = 1002;
	static public final int CANNOT_MODIFY_FRIEND_UID = 1003;
	static public final int WRONG_FRIEND_UID = 1004;
	static public final int CANNOT_DEL_POS_HAVE_SUBS = 1005;
	static public final int CNG_POS_OWNER_OWNER_NOT_EXIST = 1006;
}
