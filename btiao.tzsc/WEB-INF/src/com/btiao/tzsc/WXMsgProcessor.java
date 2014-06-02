package com.btiao.tzsc;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.WXMsg.Text;

public class WXMsgProcessor {
	static String helpStr = "������Ϣ��ͼƬ��������Ҫ��������Ʒ\n" +
							"�������� 0 ȡ������\n" +
							"�������� 1 �ύ\n\n" +
							
							"�������� 5 �鿴�Լ������Ĵ�������Ʒ\n\n" +
							
							"����\"�� xxx\"����ʼ��������Ҫ�İ�";
	
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
