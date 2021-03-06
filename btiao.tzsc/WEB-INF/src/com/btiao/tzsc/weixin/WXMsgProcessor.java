package com.btiao.tzsc.weixin;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.service.GlobalParam;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.Tip;
import com.btiao.tzsc.weixin.WXMsg.Text;

public class WXMsgProcessor {
	public WXMsgProcessor(long areaId) {
		this.areaId = areaId;
	}
	
	public void proc(WXMsg reqWxMsg, HttpServletResponse rsp) throws Exception {
		rsp.setCharacterEncoding("UTF-8");
		
		if (reqWxMsg == null) {
			WXMsg unSupportedMsg = getTextMsg(reqWxMsg, Tip.get().unSupportedMsgType);
			rsp.getOutputStream().write(WXMsgFactory.genXML(unSupportedMsg).getBytes());
			return;
		}
		
		//当系统处于升级中时，提示给客户。
		if (GlobalParam.blockAllInput) {
			new WXApi().sendWXTxtMsg(reqWxMsg.fromUserName, Tip.get().systemUpgrade);
			return;
		}
		
		if (reqWxMsg instanceof WXMsg.Text) {
			processTextMsg((Text) reqWxMsg, rsp.getOutputStream());
		} else if (reqWxMsg instanceof WXMsg.Picture){
			processPicMsg(areaId, (WXMsg.Picture) reqWxMsg, rsp.getOutputStream());
		} else if (reqWxMsg instanceof WXMsg.SubEvent) {
			MyLogger.getAccess().info("get a subevent:"+((WXMsg.SubEvent)reqWxMsg).event);
			WXMsg helpMsg = getHelpMsg(reqWxMsg);
			new WXApi().sendRspWXMsg(helpMsg, rsp.getOutputStream());
		} else if (reqWxMsg instanceof WXMsg.Click) {
			processClickMsg(reqWxMsg, rsp.getOutputStream());
		} else {
			WXMsg unSupportedMsg = getTextMsg(reqWxMsg, Tip.get().unSupportedMsgType);
			//rsp.getOutputStream().write(WXMsgFactory.genXML(unSupportedMsg).getBytes());
			new WXApi().sendRspWXMsg(unSupportedMsg, rsp.getOutputStream());
		}
	}
	
	private void processClickMsg(WXMsg reqWxMsg, OutputStream out) throws Exception {
		String userName = reqWxMsg.fromUserName;
		String clickKey = ((WXMsg.Click)reqWxMsg).key;
		String ret = null;
		
		if (clickKey.equals("act_publish")) {
			try {
				Thread.sleep(2*1000);
			} catch (Exception e) {}
			ret = WXPutStateMgr.instance(areaId).endPut(userName);
		} else if (clickKey.equals("act_cancelpub")) {
			ret = WXPutStateMgr.instance(areaId).cancelPut(userName);
		} else if (clickKey.equals("act_browse")) {
			WXMsg retMsg = WXPutStateMgr.instance(areaId).more(userName);
			if (retMsg == null) {
				retMsg = WXPutStateMgr.instance(areaId).getall(userName);

				if (retMsg != null) {
					retMsg.createTime = System.currentTimeMillis();
					retMsg.fromUserName = reqWxMsg.toUserName;
					retMsg.toUserName = userName;
					retMsg.msgId = reqWxMsg.msgId;
					new WXApi().sendWXMsg(retMsg);
					//out.write(WXMsgFactory.genXML(retMsg).getBytes());
					//new WXApi().sendRspWXMsg(retMsg, out);
					return;
				} else {
					ret = Tip.get().nopublish;
				}
			} else {
				retMsg.createTime = System.currentTimeMillis();
				retMsg.fromUserName = reqWxMsg.toUserName;
				retMsg.toUserName = userName;
				retMsg.msgId = reqWxMsg.msgId;
				new WXApi().sendWXMsg(retMsg);
				//out.write(WXMsgFactory.genXML(retMsg).getBytes());
				//new WXApi().sendRspWXMsg(retMsg, out);
				return;
			}
		} else if (clickKey.equals("act_salehelp")) {
			WXMsg helpMsg = getHelpMsg(reqWxMsg);
			new WXApi().sendWXMsg(helpMsg);
			return;
		} else {
			ret = Tip.get().unknownClickAct;
			MyLogger.getAttackLog().warn("receive a unknown click act:"+clickKey);
		}
		
		WXMsg.Text retMsg = new WXMsg.Text();
		retMsg.content = ret;
		retMsg.fromUserName = reqWxMsg.toUserName;
		retMsg.toUserName = userName;
		retMsg.createTime = System.currentTimeMillis();
		retMsg.msgId = reqWxMsg.msgId;
		
		new WXApi().sendWXMsg(retMsg);
		//out.write(WXMsgFactory.genXML(retMsg).getBytes());
		//new WXApi().sendRspWXMsg(retMsg, out);
	}
	
	private void processPicMsg(long areaId, WXMsg.Picture reqWxMsg, OutputStream out) throws Exception {
		String ret = WXPutStateMgr.instance(areaId).putPicUrlMsg(reqWxMsg.fromUserName, reqWxMsg.picUrl);
		WXMsg retMsg = getTextMsg(reqWxMsg, ret);
		
		new WXApi().sendRspWXMsg(retMsg, out);
	}
	
	private void processTextMsg(final WXMsg.Text reqWxMsg, OutputStream out) throws Exception  {
		String ret = WXPutStateMgr.instance(areaId).putTextMsg(reqWxMsg.fromUserName, reqWxMsg.content);
		WXMsg retMsg = getTextMsg(reqWxMsg, ret);
		new WXApi().sendRspWXMsg(retMsg, out);
	}
	
	private WXMsg getTextMsg(WXMsg req, String txt) {
		WXMsg.Text ret = new WXMsg.Text();
		ret.content = txt;
		ret.createTime = req.createTime;
		ret.fromUserName = req.toUserName;
		ret.msgId = req.msgId;
		ret.toUserName = req.fromUserName;
		
		return ret;
	}
	
	static public WXMsg getHelpMsg(WXMsg req) {
		WXMsg.PicText ret = new WXMsg.PicText();
		WXMsg.PicText.Item item = new WXMsg.PicText.Item();
		
		item.desc = Tip.get().saleHelpStr;
		item.picUrl = "http://"+WXApiSession.serverip+"/btiao/tzsc/webs/tzscimg/helpx.png";
		item.title = Tip.get().helpPicTitle;

		ret.items.add(item);
		
		ret.createTime = req.createTime;
		ret.fromUserName = req.toUserName;
		ret.toUserName = req.fromUserName;
		ret.msgId = req.msgId;
		
		return ret;
	}
	
	private final long areaId;
}
