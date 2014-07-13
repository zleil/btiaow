package com.btiao.tzsc.weixin;

import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.btiao.tzsc.service.MyLogger;

/**
 * 使用它来获取微信会话凭证，用于调用微信接口。
 * @author rebecca
 *
 */
public class WXApiSession {
	static public String serverip = "182.92.81.56";
	static public String appId = "wxb064780e6b5d244d";
	static public String secret = "2fafd29d2366f0a7b05d5f439f9fea02";
		
	static private WXApiSession inst = null;
	
	static synchronized WXApiSession instance() {
		if (inst == null) {
			inst = new WXApiSession();
		}
		return inst;
	}
	
	static public void main(String[] args) {
		System.out.println(WXApiSession.instance().getToken());
	}
	
	static CloseableHttpClient getAHttpClient() throws KeyManagementException, NoSuchAlgorithmException {
		//KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                //.loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "SSLv3" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        
        return httpclient;
	}
	
	public String getToken() {
		long curTime = System.currentTimeMillis();
		if ((curTime-startTime)/1000 > (timeout - timeoutShrink)) {
			fetchFromWX();
		}
		
		return token;
	}
	
	private synchronized void fetchFromWX() {
		long curTime = System.currentTimeMillis();
		if ((curTime-startTime)/1000 < (timeout - timeoutShrink)) {
			return; //now the token was fetched out just now, so use the exist.
		}
		
		try {
			fetch();
		} catch (Exception e) {
			MyLogger.get().warn("fetch weixin access token faled!", e);
		}
	}
	
	/**
	 * 获取微信access token，填写到token成员变量内。
	 * @throws Exception
	 */
	private void fetch() throws Exception {
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appId+"&secret="+secret;

		CloseableHttpClient httpclient = getAHttpClient();
		
        try {

            HttpGet httpget = new HttpGet(url);

            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                InputStream input = entity.getContent();
                long len = entity.getContentLength();
                byte[] buf = new byte[512];
                int size = input.read(buf, 0, (int)len);
                String ret = new String(buf, 0, size);
                
                int idx1 = ret.indexOf(":") + 2;
                int idx2 = ret.indexOf(",") - 1;
                token = ret.substring(idx1, idx2);
                
                startTime = System.currentTimeMillis();
                
                MyLogger.get().info("get a new weixin access token:" + token);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
	}
	
	private WXApiSession() {}
	
	private String token = null;
	private long timeout = 7200; //second
	private long startTime = 0;
	
	private long timeoutShrink = 60; //second
	
	
}
