package com.btiao.tzsc.weixin;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import com.btiao.tzsc.service.MyLogger;

public class WXApi {
	static public void main(String[] args) throws Exception {
		WXMsg.Text msg = new WXMsg.Text();
		msg.toUserName = "oQZIBj4Gbn__DoSZwcdKe3SKt4BE";
		msg.content = "hello";
		new WXApi().sendWXMsg(msg);
	}
	public void sendWXMsg(WXMsg msg) throws Exception {
		String str = WXMsgFactory.genJsonStr(msg);
		if (!str.equals("")) {
			sendWXMsgStr(str);
			sendWXMsgInfo();
		}
	}
	
	private void sendWXMsgStr(String str) throws Exception {
		String token = WXSession.instance().getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token;
		CloseableHttpClient httpclient = WXSession.getAHttpClient();
		try {
			HttpPost post = new HttpPost(url);
			HttpEntity reqEntity = EntityBuilder.create().setText(str).build();
			post.setEntity(reqEntity);
			
			CloseableHttpResponse rsp = httpclient.execute(post);
			
			long len = rsp.getEntity().getContentLength();
			byte[] buf = new byte[((int)len)];
			InputStream input = rsp.getEntity().getContent();
			input.read(buf, 0, (int) len);
			
			MyLogger.get().info("\n"+url+"\n"+str);
			MyLogger.get().info(new String(buf, "UTF-8"));
		} finally {
			httpclient.close();
		}
	}
	
	private void sendWXMsgInfo() throws Exception {
		String token = WXSession.instance().getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+token+"&openid=oQZIBj4Gbn__DoSZwcdKe3SKt4BE";
		CloseableHttpClient httpclient = WXSession.getAHttpClient();
		try {
			HttpGet get = new HttpGet(url);
			
			CloseableHttpResponse rsp = httpclient.execute(get);
			
			long len = rsp.getEntity().getContentLength();
			byte[] buf = new byte[((int)len)];
			InputStream input = rsp.getEntity().getContent();
			input.read(buf, 0, (int) len);
			
			MyLogger.get().info("\n"+url);
			MyLogger.get().info(new String(buf, "UTF-8"));
		} finally {
			httpclient.close();
		}
	}
}
