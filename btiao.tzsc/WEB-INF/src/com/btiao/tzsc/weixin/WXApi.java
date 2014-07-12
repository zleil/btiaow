package com.btiao.tzsc.weixin;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONObject;

import com.btiao.tzsc.service.MyLogger;

public class WXApi {
	static public void main(String[] args) throws Exception {
		WXMsg.Text msg = new WXMsg.Text();
		msg.toUserName = "oQZIBj4Gbn__DoSZwcdKe3SKt4BE";
		msg.content = "hello";
		
		WXApi api = new WXApi();
		api.sendWXMsg(msg);
		
		api.getUserInfo("oQZIBj4Gbn__DoSZwcdKe3SKt4BE");
	}
	
	public void sendWXMsg(WXMsg msg) throws Exception {
		String str = WXMsgFactory.genJsonStr(msg);
		if (!str.equals("")) {
			return;
		}
		
		String token = WXApiSession.instance().getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token;
		CloseableHttpClient httpclient = WXApiSession.getAHttpClient();
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
	
	/**
	 * 根据openId获取用户基本信息
	 * @param openId
	 * @throws Exception
	 */
	public WXUserInfo getUserInfo(String openId) throws Exception {
		String token = WXApiSession.instance().getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+token+"&openid="+openId;
		CloseableHttpClient httpclient = WXApiSession.getAHttpClient();
		try {
			HttpGet get = new HttpGet(url);
			
			CloseableHttpResponse rsp = httpclient.execute(get);
			
			long len = rsp.getEntity().getContentLength();
			byte[] buf = new byte[((int)len)];
			InputStream input = rsp.getEntity().getContent();
			input.read(buf, 0, (int) len);
			
			MyLogger.get().info("\n"+url);
			MyLogger.get().info(new String(buf, "UTF-8"));
			
			WXUserInfo uinfo = new WXUserInfo();
			
			JSONObject jsonobj = new JSONObject(new String(buf, "UTF-8"));
			uinfo.openId = jsonobj.getString("openid");
			uinfo.nick = jsonobj.getString("nickname");
			
			return uinfo;
		} finally {
			httpclient.close();
		}
	}
}
