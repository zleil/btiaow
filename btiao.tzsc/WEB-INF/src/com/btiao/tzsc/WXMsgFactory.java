package com.btiao.tzsc;

import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

public class WXMsgFactory {
	static public void main(String[] args) {
		String xml = "<xml>" +
			"<ToUserName><![CDATA[toUser]]></ToUserName>" +
			"<FromUserName><![CDATA[fromUser]]></FromUserName>" +
			"<CreateTime>1348831860</CreateTime>" +
			"<MsgType><![CDATA[text]]></MsgType>" +
			"<Content><![CDATA[this is a test]]></Content>" +
			"<MsgId>1234567890123456</MsgId>" +
			"</xml>";
		WXMsg.Text msg = (WXMsg.Text)WXMsgFactory.gen(xml);
		System.out.println(msg.content);
	}
	
	static public String genXML(WXMsg msg) {
		if (msg instanceof WXMsg.Text) {
			return "<xml>" +
					"<ToUserName><![CDATA["+msg.toUserName+"]]></ToUserName>" +
					"<FromUserName><![CDATA["+msg.fromUserName+"]]></FromUserName>" +
					"<CreateTime>"+msg.createTime+"</CreateTime>" +
					"<MsgType><![CDATA[text]]></MsgType>" +
					"<Content><![CDATA["+((WXMsg.Text)msg).content+"]]></Content>" +
					"<MsgId>"+msg.msgId+"</MsgId>" +
					"</xml>";
		} else if (msg instanceof WXMsg.PicText) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml><ToUserName><![CDATA["+msg.toUserName+"]]></ToUserName>");
			sb.append("<FromUserName><![CDATA["+msg.fromUserName+"]]></FromUserName>");
			sb.append("<CreateTime>"+msg.createTime+"</CreateTime>");
			sb.append("<MsgType><![CDATA[news]]></MsgType>");
			sb.append("<ArticleCount>"+((WXMsg.PicText)msg).items.size()+"</ArticleCount>");
			sb.append("<Articles>");
			for (WXMsg.PicText.Item item : ((WXMsg.PicText) msg).items) {
				sb.append("<item><Title><![CDATA["+item.title+"]]></Title>");
				sb.append("<Description><![CDATA["+item.desc+"]]></Description>");
				if (item.picUrl != null) sb.append("<PicUrl><![CDATA["+item.picUrl+"]]></PicUrl>");
				if (item.url != null) sb.append("<Url><![CDATA["+item.url+"]]></Url></item>");
			}
			sb.append("</Articles></xml>");
			
			return sb.toString();
		} else {
			MyLogger.get().error("genXML: there 's maybe a error!"+msg.toString());
			return "";
		}
	}
	
	static public WXMsg gen(String xml) {
		SAXBuilder jdomBuilder = new SAXBuilder();
		try {
			Document doc = jdomBuilder.build(new StringReader(xml));
			
			Element root = doc.getRootElement();
			String type = root.getChildText("MsgType");
			
			WXMsg msg = null;
			
			if (type.equals("text")) {
				msg = new WXMsg.Text();
				((WXMsg.Text)msg).content = root.getChildText("Content");
			} else if (type.equals("image")) {
				msg = new WXMsg.Picture();
				((WXMsg.Picture)msg).picUrl = root.getChildText("PicUrl");
				((WXMsg.Picture)msg).mediaId = root.getChildText("MediaId");
			}
			
			msg.createTime = Long.parseLong(root.getChildText("CreateTime"));
			msg.fromUserName = root.getChildText("FromUserName");
			msg.msgId = root.getChildText("MsgId");
			msg.toUserName = root.getChildText("ToUserName");
			
			return msg;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
