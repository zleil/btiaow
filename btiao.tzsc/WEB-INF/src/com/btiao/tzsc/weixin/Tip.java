package com.btiao.tzsc.weixin;

public class Tip {
	static private Tip inst;
	static public synchronized Tip get() {
		if (inst == null) {
			inst = new Tip();
		}
		return inst;
	}
	
	private Tip() {}
	
	
	public String continuePutTip = "发送文字、图片，继续描述物品\n\n" + 
			"发送数字 0 ，取消描述\n" +
			"发送数字 1 ，提交物品信息";
	
	public String noPhoneNumDescErrorTip = "抱歉，还需您提供手机号码";
	
	public String welcomeStr = "欢迎访问便条网跳蚤市场～";
	
	public String helpStr = "发送文字、图片，描述您要发布的物品，再提交";
	
	public String phoneNumFillHelpTip = "提示：发送'@13812345678'，提供您的手机号13812345678";
	
	public String noFirstTextDescError = "亲，得先用文字描述下物品呦";
	
	public String cancelSuccess = "取消成功";
	
	public String putSuccessTip = "您又多了件物品，候着邻居来买吧\n\n您当前的物品数是：";
	
	public String delStateSuccess = "删除成功\n\n您当前的待交换物品数是：";
	
	public String delStateFailed = "您要删除的物品不存在\n\n您当前的待交换物品数是：";
	
	public String doOtherActWhenPutErrorTip = "您正在描述物品，请先取消或提交，再进行其他“搜索”之类的操作\n\n"+
			"发送数字 0 ，取消描述\n" + 
			"发送数字 1 ，提交物品信息";
	
	public String noMoreDataTip = "没有更多的信息了～";
	
	public String hoursBefore = " 小时前";
	public String minutesBefore = " 分钟前";
	public String rightNow = "刚才发布";
	
	public String reachMaxSwitch = "抱歉，系统限制最多存在8个待交换物品～";
	
	public String moreTip = "还有很多呢，继续 '找找看' 吧";
	public String allReturnedTip = "就这么多了，总件数：";
	
	public String thanksSuggestion = "感谢您提供宝贵的建议。";
	
	public String nopublish = "所以物品都卖完了，真是太热闹！";
	
	public String mianZeNote = "小伙伴们，跳蚤市场暂无实力校对每样物品，您要自行与卖主交流识别好物品和价钱哦~";
	
	public String tzscTitle = "便条网 - 跳蚤市场";
}
