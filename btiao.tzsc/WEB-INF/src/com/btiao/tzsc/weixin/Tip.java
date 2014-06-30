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
	
	public String noPhoneNumDescErrorTip = "对不起，您还没有提供电话号码";
	
	public String welcomeStr = "欢迎访问便条网跳蚤市场～";
	
	public String helpStr = "发送文字、图片，描述待交换物品\n\n" +
							
							"发送数字 5 ，查看您的物品\n" +
							"发送数字 3 x ，删除第x件物品\n\n" +
							
							"发送\"搜索 xxx\"，搜索xxx物品";
	
	public String phoneNumFillHelpTip = "单独发送以@开头的信息，表示您要输入您的电话号码，@符号子后必须是9位数字哦～";
	
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
	
	public String moreTip = ">>还有更多呢!\n发送数字 8 ，显示更多搜到的物品";
}
