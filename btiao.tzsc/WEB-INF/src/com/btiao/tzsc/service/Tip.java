package com.btiao.tzsc.service;

public class Tip {
	static private Tip inst;
	static public synchronized Tip get() {
		if (inst == null) {
			inst = new Tip();
		}
		return inst;
	}
	
	private Tip() {}
	
	public String busy = "系统忙...";
	
	public String continuePutTip = "收到喽~\n\n发送文字、图片，继续描述物品\n\n或者点击“卖东西” -> 提交，立即发布";
	
	public String noPhoneNumDescErrorTip = "抱歉，还需您提供手机号码";
	
	public String welcomeStr = "欢迎访问便条网跳蚤市场～";
	
	public String notDescInPublish = "朋友，请先提交物品描述，再点击“提交”哦~\n\n详见“帮助”";

	public String cancelSuccess = "取消成功";
	
	public String putSuccessTip = "又多了件物品，真是令人激动啊~\n\n候着邻居来买吧\n\n您的当前物品总数：";
	
	public String delStateSuccess = "删除成功\n\n您当前的待交换物品数是：";
	
	public String delStateFailed = "您要删除的物品不存在\n\n您当前的待交换物品数是：";

	public String noMoreDataTip = "没有更多的信息了～";
	
	public String daysBefore = " 天前";
	public String hoursBefore = " 小时前";
	public String minutesBefore = " 分钟前";
	public String rightNow = "刚才发布";
	
	public String reachMaxSwitch = "朋友，最多允许8个待交换物品呦~\n\n点击“更多” -> “我的物品”，将已成交物品、不想交换的物品标记下。";
	
	public String moreTip = "还有很多呢，继续 '淘' 吧";
	public String allReturnedTip = "以上是全部待卖物品，总件数：";
	
	public String thanksSuggestion = "感谢您提供宝贵的建议。";
	
	public String nopublish = "所以物品都卖完了，真是太热闹！";
	
	public String tzscTitle = "便条网 - 跳蚤市场";
	
	public String saleHelpStr = "发布一件物品，仅需两步:\n" +
								"1.发送物品的“文字、图片”信息到本订阅号\n" + 
								"2.点击“卖” -> “提交”";
	
	public String helpPicTitle = "试试看：左下角按钮，多按几次，知道在哪发送“文字、图片”了吧 :-）";
	
	public String unknownClickAct = "朋友，这个功能正在实现中哦，敬请等待:-)";
	
	public String unSupportedMsgType = "朋友，当前仅支持文本、图片消息哦~";
	
	public String notDengji = "朋友，您还未的登记个人信息呦~\n\n登记后才能发布待交换信息~\n\n点击“更多” -> “登记摊位”，完成登记";
	
	public String dengjiAndNotPass = "朋友，您登记的信息暂时还未被审核通过呦~\n\n我们正努力为您服务，稍等下...";
	
	public String homeIdHaveBeenUsed = "您登记的房间已经被登记过了，我们会尽快联系您进行审核确认...";
	
	public String curPublishTimeout = "朋友，您需要在一定的时间内完成一件物品的发布呦，现在已经超时了~";
}
