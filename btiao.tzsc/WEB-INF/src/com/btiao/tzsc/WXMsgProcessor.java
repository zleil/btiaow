package com.btiao.tzsc;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.WXMsg.Text;

public class WXMsgProcessor {
	static String helpStr = "发送消息、图片，描述您要交换的物品\n" +
							"发送数字 0 取消描述\n" +
							"发送数字 1 提交\n\n" +
							
							"发送数字 5 查看自己发布的待交换商品\n\n" +
							
							"发送\"搜 xxx\"，开始淘淘您想要的吧";
	
	public void proc(WXMsg msg, HttpServletResponse rsp) throws Exception {
		if (msg instanceof WXMsg.Text) {
			processTextMsg((Text) msg, rsp.getOutputStream());
		} else {
			WXMsg.Text ret = new WXMsg.Text();
			ret.content = helpStr;
			ret.createTime = msg.createTime;
			ret.fromUserName = msg.toUserName;
			ret.msgId = msg.msgId;
			ret.toUserName = msg.msgId;
			
			rsp.getOutputStream().write(WXMsgFactory.genXML(ret).getBytes());
		}
	}
	
	private void processTextMsg(WXMsg.Text msg, OutputStream out) throws Exception  {
		String userName = msg.fromUserName;
		String content = msg.content;
		
		String ret = WXPutStateMgr.instance().putTextMsg(userName, content);
		msg.content = ret;
		msg.fromUserName = msg.toUserName;
		msg.toUserName = userName;
		
		String retXML = WXMsgFactory.genXML(msg);
		
		MyLogger.get().debug("response msg:\n"+retXML);
		out.write(retXML.getBytes());
	}
}
