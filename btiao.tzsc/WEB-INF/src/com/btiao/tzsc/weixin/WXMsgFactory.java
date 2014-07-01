package com.btiao.tzsc.weixin;

import java.io.StringReader;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.weixin.WXMsg.PicText;
import com.btiao.tzsc.weixin.WXMsg.Text;

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
	
	static public String genJsonStr(WXMsg msg) {
		if (msg instanceof WXMsg.PicText) {
			return genWXPicTextMsgJsonStr((PicText) msg);
		} else if (msg instanceof WXMsg.Text){
			return genWXTextMsgJsonStr((Text) msg);
		} else {
			MyLogger.get().error("genJsonStr: do not implement it, "+msg.getClass().getName());
			return "";
		}
	}
	
	static private String genWXTextMsgJsonStr(WXMsg.Text msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"touser\":\""+msg.toUserName+
				"\",\"msgtype\":\"text\"," +
				"\"text\":{\"content\":\""+msg.content+"\"}}");
		return sb.toString();
	}
	
	static private String genWXPicTextMsgJsonStr(WXMsg.PicText msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("{{\"touser\":\""+msg.toUserName+"\",");
		sb.append("\"msgtype\":\"news\",\"news\":{\"articles\":[");
		
		boolean first = true;
		for (WXMsg.PicText.Item item : ((WXMsg.PicText)msg).items) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			
			sb.append("{");
			
			boolean f = false;
			if (item.title != null) {
				if (!f) {
					f = true;
				} else {
					sb.append(",");
				}
				sb.append("\"title\":\""+item.title+"\"");
			}
			if (item.desc != null) {
				if (!f) {
					f = true;
				} else {
					sb.append(",");
				}
				sb.append("\"description\":\"").append(item.desc).append("\"");
			}
			if (item.url != null) {
				if (!f) {
					f = true;
				} else {
					sb.append(",");
				}
				sb.append("\"url\":\"").append(item.url).append("\"");
			}
			if (item.picUrl != null) {
				if (!f) {
					f = true;
				} else {
					sb.append(",");
				}
				sb.append("\"picurl\":\"").append(item.picUrl).append("\"");
			}
			
			sb.append("}");
		}
		sb.append("]}}");
		
		return sb.toString();
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
				sb.append("<item>");
				if (item.title != null) sb.append("<Title><![CDATA["+item.title+"]]></Title>");
				if (item.desc != null) sb.append("<Description><![CDATA["+item.desc+"]]></Description>");
				if (item.picUrl != null) sb.append("<PicUrl><![CDATA["+item.picUrl+"]]></PicUrl>");
				if (item.url != null) sb.append("<Url><![CDATA["+item.url+"]]></Url>");
				sb.append("</item>");
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
				
				msg.msgId = root.getChildText("MsgId");
			} else if (type.equals("image")) {
				msg = new WXMsg.Picture();
				((WXMsg.Picture)msg).picUrl = root.getChildText("PicUrl");
				((WXMsg.Picture)msg).mediaId = root.getChildText("MediaId");
				
				msg.msgId = root.getChildText("MsgId");
			} else if (type.equals("event")) {
				msg = new WXMsg.Event();
				((WXMsg.Event)msg).event = root.getChildText("Event");
			} else {
				return null;
			}
			
			msg.createTime = Long.parseLong(root.getChildText("CreateTime"));
			msg.fromUserName = root.getChildText("FromUserName");
			msg.toUserName = root.getChildText("ToUserName");
			
			return msg;
		} catch (Throwable e) {
			MyLogger.get().warn("gen WXMsg object failed!\n" + xml, e);
		}
		
		return null;
	}
}
