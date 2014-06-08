package com.btiao.tzsc.weixin;

import java.util.ArrayList;
import java.util.List;

public class WXMsg {
	public String toUserName;
	public String fromUserName;
	public long createTime;
	public String msgId;
	
	public static class Text extends WXMsg {
		public String content;
	}
	
	public static class Picture extends WXMsg {
		public String picUrl;
		public String mediaId;
	}
	
	public static class PicText extends WXMsg {
		public static class Item {
			public String title;
			public String desc;
			public String picUrl;
			public String url;
		}
		
		public List<Item> items = new ArrayList<Item>();
	}
}
