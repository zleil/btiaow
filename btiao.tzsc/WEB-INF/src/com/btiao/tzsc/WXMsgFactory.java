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
				return msg;
			}
			
			msg.createTime = Long.parseLong(root.getChildText("CreateTime"));
			msg.fromUserName = root.getChildText("FromUserName");
			msg.msgId = root.getChildText("MsgId");
			msg.toUserName = root.getChildText("ToUserName");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
