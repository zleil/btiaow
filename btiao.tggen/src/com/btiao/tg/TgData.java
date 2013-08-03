package com.btiao.tg;

import java.util.ArrayList;
import java.util.List;

public class TgData {
	public static class TgType {
		static public final int unkown = 0; //不能识别的，若出现则需要处理
		static public final int food = 1; //美食 [1,10000)
		static public final int food_huoguo = 1000; //美食-火锅
		static public final int food_kaoyu = 1001; //美食-烤鱼
		static public final int food_kaorou = 1002; //美食-烤肉 
		static public final int food_mlxg = 1003; //美食-麻辣香锅
		static public final int food_xican = 1004; //美食-西餐
		static public final int food_taicai = 1005; //美食-泰菜
		static public final int food_dangao = 1006; //美食-蛋糕
		static public final int food_kaoya = 1007; //美食-烤鸭
		static public final int food_ice = 1008; //美食-冰淇淋
		
		static public final int ktv = 10000; //KTV
		
		static public final int film = 20000; //电影
		static public final int film_child = 20001; //电影-儿童片
		
		static public final int tiyu = 30000; //体育
		static public final int tiyu_jianshen = 30001; //体育-健身
		static public final int tiyu_youyong = 30002; //体育-游泳
		
		static public final int yangs = 40000; //养身
		static public final int yangs_spa = 40001; //养身-SPA
		
		static public final int jiuba = 50000; //酒吧
		
		static public final int mei = 60000; //美容美发
		static public final int mei_meijia = 60000; //美容美发-美甲
		
		static public final int juyuan = 70000; //剧院
		static public final int juyuan_yinyue = 70001; //剧院-音乐剧
		
		static public final int sheying = 70000; //摄影
		static public final int sheying_xiezhen = 70001; //摄影-写真
		static public final int sheying_hunsha = 70002; //摄影-婚纱
		
	};
	
	public int type; //团购类型，取值范围见：TgType
	
	public String url;
	
	public int longitude;
	public int latitude;
	
	public String title;
	public String desc;
	
	public String imageUrl;
	
	//时间信息
	public long startTime;
	public long endTime;
	public long useEndTime; //最晚消费时间 
	
	//价格信息
	public int value;
	public int price;
	public int boughtNum; //当前购买人数
//	public int maxQuota; //最多购买人数
//	public int minQuota; //最少购买人数
	
	//其他信息
//	public int bitType; //第0位为是否支持投递
	
	//团购来源
//	public String nameOfOrigWebSite;
//	public String urlOfOrigWebSite;
	
	public int dist = 1000*1000; //距离，默认表示未知
	public String shopName = ""; //商家名称
}
