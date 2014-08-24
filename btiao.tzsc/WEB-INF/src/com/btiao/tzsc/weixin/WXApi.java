package com.btiao.tzsc.weixin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.btiao.tzsc.service.Tip;

public class WXApi {
	static public void main(String[] args) throws Exception {
//		WXMsg.Text msg = new WXMsg.Text();
//		msg.toUserName = "oQZIBj4Gbn__DoSZwcdKe3SKt4BE";
//		msg.content = "hello";
//		
		WXApi api = new WXApi();
//		api.sendWXMsg(msg);
//		
		WXMsg.PicText pictxt = new WXMsg.PicText();
		pictxt.toUserName = "oQZIBj4Gbn__DoSZwcdKe3SKt4BE";
		WXMsg.PicText.Item item = new WXMsg.PicText.Item();
		item.desc = Tip.get().saleHelpStr;
		item.picUrl = "http://"+WXApiSession.serverip+"/btiao/tzsc/webs/tzscimg/helpall2.png";
		item.title = "";
		pictxt.items.add(item);
		api.sendWXMsg(pictxt);
//		
//		api.getUserInfo("oQZIBj4Gbn__DoSZwcdKe3SKt4BE");
//		api.createMenu(65537, WXApi.class.getResource("menu.json").getPath());
	}
	
	public void sendWXTxtMsg(String dstOpenId, String txt) {
		WXMsg.Text msg = new WXMsg.Text();
		msg.content = txt;
		msg.createTime = System.currentTimeMillis();
		msg.toUserName = dstOpenId;
		
		try {
			new WXApi().sendWXMsg(msg);
		} catch (Exception e) {
			MyLogger.get().warn("failed to send timeout msg to user:" + dstOpenId+":"+txt, e);
		}
	}
		
	public int sendWXMsg(WXMsg msg) throws Exception {
		String str = WXMsgFactory.genJsonStr(msg);
		if (str.equals("")) {
			MyLogger.get().error("failed to convert msg object to string while sending it:"+msg);
			return -1;
		}

		String token = WXApiSession.instance().getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token;
		return post(url, str).getInt("errcode");
	}
	
	public void sendRspWXMsg(WXMsg msg, OutputStream out) throws Exception{
		WXMsg.Text rsp = new WXMsg.Text();
		rsp.content = "";
		rsp.createTime = msg.createTime;
		rsp.fromUserName = msg.fromUserName;
		rsp.toUserName = msg.toUserName;
		rsp.msgId = msg.msgId;
		
		String msgxml = WXMsgFactory.genXML(rsp);
		out.write(msgxml.getBytes());
		MyLogger.get().info("sendWXMsg xml:\n"+msgxml);
		//不知道为什么上述发送代码总是发布到用户端，直接使用下述代码发送
		sendWXMsg(msg);
	}
	
	/**
	 * 根据openId获取用户基本信息
	 * @param openId
	 * @throws Exception
	 */
	public int getUserInfo(String openId, WXUserInfo uinfo) throws Exception {
		String token = WXApiSession.instance().getToken();
		String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="+token+"&openid="+openId;

		JSONObject jsonobj = get(url);
		if (jsonobj.has("errcode")) {
			return jsonobj.getInt("errcode");
		}
		
		uinfo.openId = jsonobj.getString("openid");
		uinfo.nick = jsonobj.getString("nickname");
		
		return 0;
	}
	
	public int getUserIdFromCode(String code, WXUserInfo uinfo) throws Exception {
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+WXApiSession.appId+"&secret="+WXApiSession.secret+"&code="+code+"&grant_type=authorization_code";

		JSONObject jsonobj = get(url);
		if (jsonobj.has("errcode")) {
			return jsonobj.getInt("errcode");
		}
		
		uinfo.openId = jsonobj.getString("openid");
		uinfo.accesToken = jsonobj.getString("access_token");
		
		return 0;
	}
	
	public int createMenu(long areaId, String jsonFilePath) throws Exception {
		FileInputStream input = null;
		
		try {
			input = new FileInputStream(new File(jsonFilePath));
			byte[]buf = new byte[1024]; 
			input.read(buf);
			
			JSONObject jsonobj = new JSONObject(new String(buf));
			JSONArray buttonjso = jsonobj.getJSONArray("button");
			if (buttonjso == null) {
				System.err.println("menu.json is error!");
				return -1;
			}
			
			JSONObject my = (JSONObject) ((JSONArray)(((JSONObject)buttonjso.get(0)).getJSONArray("sub_button"))).get(3);
			String myredirectUrl = URLEncoder.encode("http://"+WXApiSession.serverip+"/btiao/tzsc/wx_managemine/"+areaId, "UTF-8");
			String appid = WXApiSession.appId;
			String myurl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+myredirectUrl+"&response_type=code&scope=snsapi_base#wechat_redirect";
			my.put("url", myurl);
			
			JSONObject dengji = (JSONObject) ((JSONArray)(((JSONObject)buttonjso.get(0)).getJSONArray("sub_button"))).get(2);
			String dengjiRedirectUrl = URLEncoder.encode("http://"+WXApiSession.serverip+"/btiao/tzsc/wx_managemine/"+areaId+"?act=dengji", "UTF-8");
			String dengjiUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appid+"&redirect_uri="+dengjiRedirectUrl+"&response_type=code&scope=snsapi_base#wechat_redirect";
			dengji.put("url", dengjiUrl);
			
			String token = WXApiSession.instance().getToken();
			String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+token;
			
			MyLogger.get().info(jsonobj.toString());
			
			JSONObject retJso = post(url, jsonobj.toString());
			if (retJso.has("errcode")) {
				return retJso.getInt("errcode");
			}
			
			return 0;
		} finally {
			if (input != null) {
				input.close();
			}
		}
	}
	
	private JSONObject post(String url, String entityArgs) throws Exception {
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
			
			MyLogger.get().info("post to wx:"+url+"\n"+entityArgs);
			MyLogger.get().info("resp from wx:\n"+new String(buf, "UTF-8"));
			
			return new JSONObject(new String(buf)); 
		} finally {
			httpclient.close();
		}
	}
	
	private JSONObject get(String url) throws Exception {
		CloseableHttpClient httpclient = WXApiSession.getAHttpClient();
		try {
			HttpGet get = new HttpGet(url);
			
			CloseableHttpResponse rsp = httpclient.execute(get);
			
			long len = rsp.getEntity().getContentLength();
			byte[] buf = new byte[((int)len)];
			InputStream input = rsp.getEntity().getContent();
			input.read(buf, 0, (int) len);
			String retStr = new String(buf, "UTF-8");
			
			MyLogger.get().info("\n"+url);
			MyLogger.get().info(retStr);
			
			return new JSONObject(retStr);
		} finally {
			httpclient.close();
		}
	}
}
