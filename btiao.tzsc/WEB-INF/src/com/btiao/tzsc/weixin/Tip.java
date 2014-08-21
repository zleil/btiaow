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
	
	
	public String continuePutTip = "发送文字、图片，继续描述物品\n\n";
	
	public String noPhoneNumDescErrorTip = "抱歉，还需您提供手机号码";
	
	public String welcomeStr = "欢迎访问便条网跳蚤市场～";
	
	public String notDescInPublish = "亲，请先提交物品描述，再点击“提交”哦";
	
	public String phoneNumFillHelpTip = "提示：发送'@13812345678'，提供您的手机号13812345678";
	
	public String noFirstTextDescError = "亲，得先用文字描述下物品呦";
	
	public String cancelSuccess = "取消成功";
	
	public String putSuccessTip = "您又多了件物品，候着邻居来买吧\n\n您当前的物品数是：";
	
	public String delStateSuccess = "删除成功\n\n您当前的待交换物品数是：";
	
	public String delStateFailed = "您要删除的物品不存在\n\n您当前的待交换物品数是：";
	
	public String doOtherActWhenPutErrorTip = "您正在描述物品，请先取消或提交，再进行其他“搜索”之类的操作\n\n";
	
	public String noMoreDataTip = "没有更多的信息了～";
	
	public String hoursBefore = " 小时前";
	public String minutesBefore = " 分钟前";
	public String rightNow = "刚才发布";
	
	public String reachMaxSwitch = "亲，最多允许8个待交换物品呦~";
	
	public String moreTip = "还有很多呢，继续 '找找看' 吧";
	public String allReturnedTip = "就这么多了，总件数：";
	
	public String thanksSuggestion = "感谢您提供宝贵的建议。";
	
	public String nopublish = "所以物品都卖完了，真是太热闹！";
	
	public String mianZeNote = "小伙伴们，跳蚤市场暂无实力校对每样物品，您要自行与卖主交流识别好物品和价钱哦~";
	
	public String tzscTitle = "便条网 - 跳蚤市场";
	
	public String saleHelpStr = "发布一件物品，仅需两步:\n" +
								"1.发送文件、图片描述您要卖的物品\n" + 
								"2.点击“卖东西” -> “提交”";
	
	public String unknownClickAct = "亲，这个功能正在实现中哦，敬请等待:-)";
	
	public String unSupportedMsgType = "亲，当前仅支持文本、图片消息哦~";
	
	public String notDengji = "亲，您还未的登记个人信息呦~\n\n登记后才能发布待交换信息~\n\n点击“更多” -> “登记摊位”，完成登记";
	
	public String dengjiAndNotPass = "亲，您登记的信息暂时还未被审核通过呦~\n\n我们正努力为您服务，稍等下...";
	
	public String homeIdHaveBeenUsed = "您登记的房间已经被登记过了，我们会尽快联系您进行审核确认...";
}
