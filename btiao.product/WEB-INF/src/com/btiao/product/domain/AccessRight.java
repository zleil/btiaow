package com.btiao.product.domain;

import java.util.List;
import com.btiao.infomodel.InfoMObject;

public class AccessRight extends InfoMObject {
	static public class CONST {
		static public final String gIdOwner = "Owner";
		static public final String gIdOther = "Other";
		static public final String gIdPrefix = "gid_";
	}
	static public class Right{
		static public final int ALLOW = 0;
		static public final int NOTALLOW = 1;
		
		static public final int DEFAULT = ALLOW;
	}
	static public enum Action {
		PUT,
		POST,
		DELETE,
		GUT,
		GETALL;
	}
	
	/**
	 * action id.
	 * @see Action
	 */
	public String action;
	
	/**
	 * the default value is "", it represents self right.<br>
	 * otherwise, it represents sub resource right of self.<br>
	 */
	public String relName = "";
	
	/**
	 * group id.<br>
	 * @see CONST
	 */
	public String gId;
	
	/**
	 * it represents the right value of the rightName.<br>
	 * @see Right
	 */
	public int value = Right.DEFAULT;

	@Override
	public boolean initId(List<String> urlIds) {
		// TODO Auto-generated method stub
		return false;
	}
}
