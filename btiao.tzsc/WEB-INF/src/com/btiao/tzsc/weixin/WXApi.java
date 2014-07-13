package com.btiao.tzsc.weixin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.btiao.tzsc.service.MyLogger;

public class WXApi {
	static public void main(String[] args) throws Exception {
		WXMsg.Text msg = new WXMsg.Text();
		msg.toUserName = "oQZIBj4Gbn__DoSZwcdKe3SKt4BE";
		msg.content = "hello";
		
		WXApi api = new WXApi();
		api.sendWXMsg(msg);
		
		WXMsg.PicText pictxt = new WXMsg.PicText();
		pictxt.toUserName = "oQZIBj4Gbn__DoSZwcdKe3SKt4BE";
		WXMsg.PicText.Item item = new WXMsg.PicText.Item();
		item.title = "abc";
		item.desc = "desc";
		item.picUrl = "xx";
		item.url = "http://www.baidu.com";
		pictxt.items.add(item);
		api.sendWXMsg(pictxt);
		
		//api.getUserInfo("oQZIBj4Gbn__DoSZwcdKe3SKt4BE");
		//api.createMenu(65537);
	}
	
	public int sendWXMsg(WXMsg msg) throws Exception {
		String str = WXMsgFactory.genJsonStr(msg);
		if (str.equals("")) {
			return -1;
		}

		String token = WXApiSession.instance().getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token;
		return post(url, str);
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
	
	private void createMenu(long areaId) throws Exception {
		FileInputStream input = null;
		
		try {
			input = new FileInputStream(new File(this.getClass().getResource("menu.json").getPath()));
			byte[]buf = new byte[1024]; 
			input.read(buf);
			
			JSONObject jsonobj = new JSONObject(new String(buf));
			JSONArray buttonjso = jsonobj.getJSONArray("button");
			if (buttonjso == null) {
				System.err.println("menu.json is error!");
				return;
			}
			
			JSONObject my = (JSONObject) buttonjso.get(2);
			String redirectUrl = URLEncoder.encode("http://"+WXApiSession.serverip+"/btiao/tzsc/wx_managemine/"+areaId, "UTF-8");
			String appid = WXApiSession.appId;
			String myurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+redirectUrl+"&response_type=code&scope=snsapi_base#wechat_redirect";
			my.put("url", myurl);
			
			String token = WXApiSession.instance().getToken();
			String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+token;
			
			MyLogger.get().info(jsonobj.toString());
			
			if (post(url, jsonobj.toString())!= 0) {
				System.out.println("create menu failed!");
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	private int post(String url, String entityArgs) throws Exception {
		CloseableHttpClient httpclient = WXApiSession.getAHttpClient();
		try {
			HttpPost post = new HttpPost(url);
			post.addHeader("Content-Encoding", "UTF-8");
			
			HttpEntity reqEntity = new StringEntity(entityArgs, "UTF-8"); //EntityBuilder.create().setText(entityArgs).build();
			post.setEntity(reqEntity);
			
			CloseableHttpResponse rsp = httpclient.execute(post);
			
			long len = rsp.getEntity().getContentLength();
			byte[] buf = new byte[((int)len)];
			InputStream input = rsp.getEntity().getContent();
			input.read(buf, 0, (int) len);
			
			MyLogger.get().info("wxpost:"+url+"\n"+entityArgs);
			MyLogger.get().info("resp:\n"+new String(buf, "UTF-8"));
			
			JSONObject retJso = new JSONObject(new String(buf));
			return retJso.getInt("errcode");
		} finally {
			httpclient.close();
		}
	}
}
