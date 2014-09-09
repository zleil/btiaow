package com.btiao.tzsc.service;

/**
 * ComDataMgr所管理的元数据类型名称或前后缀定义。
 * @author zleil
 *
 */
public class MetaDataId {
	/**
	 * 微信用户关注小区微信后，首先需要进行登记，之后审核通过后，再正式成为正式住户。
	 */
	static public final String dengji = "dengji";
	
	/**
	 * 用户删除的物品信息存放到此，一个用户一份数据
	 */
	static public final String deletewpPrefix = "usrDelState.";
}
