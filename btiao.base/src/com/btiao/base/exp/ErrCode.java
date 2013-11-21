package com.btiao.base.exp;

public class ErrCode {
	static public int SUCCESS = 0;
	static public int WRONG_PARAM = 1;
	static public int UNKOWN_ERR = 2;
	static public int FUNC_NOT_SURPPORT = 3;
	static public int AUTH_FAILED = 4;
	static public int REACHED_MAX_LOGIN_PER_USER = 5;
	static public int MAY_LOGOUTED = 6;
	
	static public int ADD_DUP_OBJ_TO_INFO_MODEL = 100;
	static public int ADD_DUP_REL_TO_INFO_MODEL = 100;
	static public int OBJ_NOT_IN_INFO_MODEL = 101;
	
	static public int INTERNEL_ERROR = 1000;
	static public int TIME_OUT = 1001;
	static public int USER_HAVE_NO_RIGHT = 1002;
}
