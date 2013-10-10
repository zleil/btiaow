package com.btiao.tg;

import java.util.Map;

public class TgShop {
	public long longitude; //经度，放大1000*1000倍
	public long latitude; //维度，放大1000*1000倍
	
	public String addr; //地址
	public String name; //名称
	public String desc; //简介
	
	public String tel; //电话
	
	public String city; //城市
	public String area; //区名
	public String shopArea; //商区名
	
	public byte star; //评价星数，最多5星，最少1星
	public Map<Integer,Integer> labelInfo; //评价标签状态，赞成的人数
}
