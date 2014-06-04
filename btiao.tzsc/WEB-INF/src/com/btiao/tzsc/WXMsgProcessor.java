package com.btiao.tzsc;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.WXMsg.Text;

public class WXMsgProcessor {
	static public String helpStr = "发送文字、图片，描述物品\n" +
							"发送数字 0 ，取消描述\n" +
							"发送数字 1 ，提交物品信息\n\n" +
							
							"发送数字 3 x ，删除您的第x个物品\n" +
							
							"发送数字 5 ，查看您的物品\n\n" +
							
							"发送\"搜索 xxx\"，搜索xxx物品\n" + 
							"发送数字 8 ，显示更多物品";
	
	
	public void proc(WXMsg msg, HttpServletResponse rsp) throws Exception {
		rsp.setCharacterEncoding("UTF-8");
		
		if (msg instanceof WXMsg.Text) {
			processTextMsg((Text) msg, rsp.getOutputStream());
		} else if (msg instanceof WXMsg.Picture){
			processPicMsg((WXMsg.Picture) msg, rsp.getOutputStream());
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
	
	private void processPicMsg(WXMsg.Picture msg, OutputStream out) throws Exception {
		String ret = WXPutStateMgr.instance().putUrlMsg(msg.fromUserName, msg.picUrl);
		
		WXMsg.Text rspMsg = new WXMsg.Text();
		rspMsg.fromUserName = msg.toUserName;
		rspMsg.toUserName = msg.fromUserName;
		rspMsg.content = ret;
		rspMsg.msgId = msg.msgId;
		rspMsg.createTime = System.currentTimeMillis();
		
		String retXML = WXMsgFactory.genXML(rspMsg);
		
		MyLogger.get().debug("processPicMsg response msg:\n"+retXML);
		out.write(retXML.getBytes());
	}
	
	private void processTextMsg(WXMsg.Text msg, OutputStream out) throws Exception  {
		String userName = msg.fromUserName;
		
		String ret = null;
		if (msg.content.startsWith("1")) {
			ret = WXPutStateMgr.instance().endPut(userName);
		} else if (msg.content.startsWith("0")) {
			ret = WXPutStateMgr.instance().cancelPut(userName);
		} else if (msg.content.startsWith("搜索")) {
			String toSearch = msg.content.substring(2).trim();
			List<WXMsg> msgs = WXPutStateMgr.instance().search(userName, toSearch);

			for (WXMsg one : msgs) {
				one.fromUserName = msg.toUserName;
				one.toUserName = userName;
				one.createTime = System.currentTimeMillis();
			}
			
			if (msgs.size() != 0) {
				//TODO only support display one msg per time.
				String retXML = WXMsgFactory.genXML(msgs.get(0));
				
				MyLogger.get().debug("processTextMsg response msg:\n"+retXML);
				out.write(retXML.getBytes());
				
				return;
			} else {
				ret = "抱歉，没找到关于 "+toSearch+" 的物品";
			}
		} else if (msg.content.startsWith("5")) {
			ret = WXPutStateMgr.instance().returnSelfAll(userName);
		} else if (msg.content.startsWith("3")) {
			int idx = -1;
			try {
				idx = Integer.parseInt(msg.content.substring(2).trim());
				ret = WXPutStateMgr.instance().delOne(userName, idx);
			} catch (NumberFormatException e) {
				ret = "发送数字 3 x ，删除您的第x个物品";
			}
			
		} else if (msg.content.startsWith("8")) {
			List<WXMsg> rets = WXPutStateMgr.instance().more(userName);
			
			for (WXMsg retMsg : rets) {
				retMsg.createTime = System.currentTimeMillis();
				retMsg.fromUserName = msg.toUserName;
				retMsg.toUserName = msg.fromUserName;
			}
			if (rets.size() != 0) {
				//TODO only support display one msg per time.
				String retXML = WXMsgFactory.genXML(rets.get(0));
								
				MyLogger.get().debug("processTextMsg response msg:\n"+retXML);
				out.write(retXML.getBytes());
				
				return;
			} else {
				ret = "所有物品均已呈现";
			}
		} else {
			ret = WXPutStateMgr.instance().putTextMsg(userName, msg.content);
		}
		
		msg.content = ret;
		msg.fromUserName = msg.toUserName;
		msg.toUserName = userName;
		
		String retXML = WXMsgFactory.genXML(msg);
		
		MyLogger.get().debug("processTextMsg response msg:\n"+retXML);
		out.write(retXML.getBytes());
	}
}
