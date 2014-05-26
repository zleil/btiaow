package com.btiao.tzsc;

public class WXMsg {
	public String toUserName;
	public String fromUserName;
	public long createTime;
	public String msgId;
	
	public static class Text extends WXMsg {
		public String content;
	}
}
