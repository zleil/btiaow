package com.btiao.tzsc;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.WXMsg.Text;

public class WXMsgProcessor {
	static String helpStr = "发送文字、图片，描述待交换物品\n" +
							"发送数字 0 ，取消描述\n" +
							"发送数字 1 ，提交待描述物品\n\n" +
							
							"发送数字 5 ，查看您的待交换物品\n\n" +
							
							"发送\"搜索 xxx\"，搜索xxx相关的物品";
	
	public void proc(WXMsg msg, HttpServletResponse rsp) throws Exception {
		rsp.setCharacterEncoding("UTF-8");
		
		if (msg instanceof WXMsg.Text) {
			processTextMsg((Text) msg, rsp.getOutputStream());
		} else {
			WXMsg.Text ret = new WXMsg.Text();
			ret.content = helpStr;
			ret.createTime = msg.createTime;
			ret.fromUserName = msg.toUserName;
			ret.msgId = msg.msgId;
			ret.toUserName = msg.msgId;
			
			System.out.println(helpStr);
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
