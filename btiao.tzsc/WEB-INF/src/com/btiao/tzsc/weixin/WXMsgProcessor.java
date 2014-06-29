package com.btiao.tzsc.weixin;

import java.io.OutputStream;
import javax.servlet.http.HttpServletResponse;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.weixin.WXMsg.Text;

public class WXMsgProcessor {
	
	public void proc(WXMsg reqWxMsg, HttpServletResponse rsp) throws Exception {
		rsp.setCharacterEncoding("UTF-8");
		
		if (reqWxMsg instanceof WXMsg.Text) {
			processTextMsg((Text) reqWxMsg, rsp.getOutputStream());
		} else if (reqWxMsg instanceof WXMsg.Picture){
			processPicMsg((WXMsg.Picture) reqWxMsg, rsp.getOutputStream());
		} else {			
			WXMsg helpMsg = getHelpMsg(reqWxMsg);
			rsp.getOutputStream().write(WXMsgFactory.genXML(helpMsg).getBytes());
		}
	}
	
	private WXMsg getHelpMsg(WXMsg req) {
		WXMsg.Text ret = new WXMsg.Text();
		ret.content = Tip.get().helpStr;
		ret.createTime = req.createTime;
		ret.fromUserName = req.toUserName;
		ret.msgId = req.msgId;
		ret.toUserName = req.fromUserName;
		
		return ret;
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
	
	private void processTextMsg(final WXMsg.Text reqWxMsg, OutputStream out) throws Exception  {
		final String userName = reqWxMsg.fromUserName;
		final String appId = reqWxMsg.toUserName;
		
		String ret = null;
		if (reqWxMsg.content.startsWith("1")) {
			ret = WXPutStateMgr.instance().endPut(userName);
		} else if (reqWxMsg.content.startsWith("0")) {
			ret = WXPutStateMgr.instance().cancelPut(userName);
		} else if (reqWxMsg.content.startsWith("搜索")) {
			String toSearch = reqWxMsg.content.substring(2).trim();
			WXMsg retMsg = WXPutStateMgr.instance().search(userName, toSearch);

			if (retMsg != null) {
				retMsg.createTime = System.currentTimeMillis();
				retMsg.fromUserName = appId;
				retMsg.toUserName = userName;
				retMsg.msgId = reqWxMsg.msgId;
				
				String retStr = WXMsgFactory.genXML(retMsg);
				MyLogger.get().debug("processTextMsg search response msg:\n"+retStr);
				out.write(retStr.getBytes());
				return;
			} else {
				ret = "抱歉，没找到关于 "+toSearch+" 的物品";
			}
		} else if (reqWxMsg.content.startsWith("5")) {
			WXMsg retMsg = WXPutStateMgr.instance().returnSelfAll(userName);
			
			if (retMsg != null) {
				retMsg.createTime = System.currentTimeMillis();
				retMsg.fromUserName = appId;
				retMsg.toUserName = userName;
				retMsg.msgId = reqWxMsg.msgId;
				
				String retStr = WXMsgFactory.genXML(retMsg);
				MyLogger.get().debug("processTextMsg search response msg:\n"+retStr);
				out.write(retStr.getBytes());
				return;
			} else {
				ret = "您没有任何交换物品，赶紧提交吧\n\n" + Tip.get().helpStr;
			}
		} else if (reqWxMsg.content.startsWith("3")) {
			int idx = -1;
			try {
				idx = Integer.parseInt(reqWxMsg.content.substring(2).trim());
				ret = WXPutStateMgr.instance().delOne(userName, idx);
			} catch (NumberFormatException e) {
				ret = "发送数字 3 x ，删除您的第x个物品";
			}
			
		} else if (reqWxMsg.content.startsWith("8")) {
			WXMsg retMsg = WXPutStateMgr.instance().more(userName);
			
			if (retMsg == null) {
				ret = "所有物品均已呈现";
			} else {
				retMsg.createTime = System.currentTimeMillis();
				retMsg.fromUserName = appId;
				retMsg.toUserName = userName;
				retMsg.msgId = reqWxMsg.msgId;
				
				String retStr = WXMsgFactory.genXML(retMsg);
				
				MyLogger.get().debug("processTextMsg8 response msg:\n"+retStr);
				
				out.write(retStr.getBytes());
				return;
			}
		} else if (reqWxMsg.content.startsWith("@")) {
			String tel = reqWxMsg.content.substring(1);
			if (!checkPhoneNum(tel)) {
				ret = WXPutStateMgr.instance().putPhoneNum(userName, tel);
			} else {
				ret = Tip.get().phoneNumErrorTip;
			}
		} else if (reqWxMsg.content.startsWith("h") || reqWxMsg.content.startsWith("帮助")) {
			WXMsg helpMsg = getHelpMsg(reqWxMsg);
			((WXMsg.Text)helpMsg).content = Tip.get().welcomeStr + "\n\n" + Tip.get().helpStr;
			MyLogger.get().debug("processTextMsg help response msg:\n"+helpMsg);
			out.write(WXMsgFactory.genXML(helpMsg).getBytes());
			
			return;
		} else {
			ret = WXPutStateMgr.instance().putTextMsg(userName, reqWxMsg.content);
		}
		
		WXMsg.Text retMsg = new WXMsg.Text();
		retMsg.content = ret;
		retMsg.fromUserName = appId;
		retMsg.toUserName = userName;
		retMsg.createTime = System.currentTimeMillis();
		retMsg.msgId = reqWxMsg.msgId;
		
		String retXML = WXMsgFactory.genXML(retMsg);
		
		MyLogger.get().debug("processTextMsg response msg:\n"+retXML);
		out.write(retXML.getBytes());
	}
	
	private boolean checkPhoneNum(String tel) {
		try {
			Integer.parseInt(tel);
		} catch (Throwable e) {
			return false;
		}
		
		return tel.length() == 9;
	}
}
