package com.btiao.tzsc.restful;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.btiao.tzsc.service.GlobalParam;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.Tip;
import com.btiao.tzsc.service.Util;

public class TZSCFilter implements Filter {
	
	private List<String> noCheckUrls = new ArrayList<String>();  

    @Override  
    public void destroy() {  
        this.noCheckUrls = null;
    }  
  
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,  
        ServletException {
    	
    	Util.logAccess((HttpServletRequest)request, "do-filter");
    	
    	//升级前先将此参数设置，然后阻止除wx_act外的所有http请求
    	if (GlobalParam.blockAllInput) {
    		String url = ((HttpServletRequest)request).getRequestURL().toString();
    		if (!url.matches("\\/wx_act\\/")) {
	    		String newUrl = "../webs/htmlconfirmtip.jsp?tip="+URLEncoder.encode(Tip.get().systemUpgrade, "UTF-8")+"&hasyes=";
				((HttpServletResponse)response).sendRedirect(newUrl);
				return;
    		}
		}
    	
    	String src = request.getRemoteHost();
    	
    	String busyUrlEncode = URLEncoder.encode(Tip.get().busy, "UTF-8");
    	try {
    		HttpServletRequest httpRequest = (HttpServletRequest)request;  
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            
            //1. 先处理不需要登陆的链接
    		String uri = httpRequest.getRequestURI();
    		for (String nocheckurl : noCheckUrls) {
    			if (uri.contains(nocheckurl)) {
    				MyLogger.get().info("here: get a nochecurl:"+nocheckurl+":uri="+uri);
    				chain.doFilter(request, response);
    				return;
    			}
    		}
    		
    		//2. 对于需要登陆的链接，要识别攻击源、采用静默手段反抗攻击源
    		if (AttackerMgr.instance().mustRefuse(src)) {
    			//String newUrl = "../webs/htmlconfirmtip.jsp?tip="+busyUrlEncode+"&hasyes=";
    			//httpResponse.sendRedirect(newUrl);
    			response.getOutputStream().write("refused!".getBytes("UTF-8"));
    			return;
    		}
    		
    		if (fromValidSession(httpRequest)) {
    			MyLogger.get().info("from a valid session:uri="+uri);
    			AttackerMgr.instance().succeed(src);
    			
    			chain.doFilter(request, response);
    			return;
    		} else {
    			AttackerMgr.instance().updateLastAttackTime(src);
    			
    			MyLogger.get().info("from a invalid session");
    			String newUrl = "../webs/htmlconfirmtip.jsp?tip="+busyUrlEncode+"&hasyes=";
    			
    			httpResponse.sendRedirect(newUrl);
    		}
    	} catch (Throwable e) {
    		MyLogger.getAttackLog().error("do filter failed!", e);
    		String newUrl = "../webs/htmlconfirmtip.jsp?tip="+busyUrlEncode+"&hasyes=";
			((HttpServletResponse)response).sendRedirect(newUrl);
    	}
    }  
  
    @Override  
    public void init(FilterConfig config) throws ServletException {   
        String urlsStr = config.getInitParameter("noCheckUrls");
        MyLogger.get().info("noCheckUrls="+urlsStr);
        
        String[] splits = urlsStr.split("\\|");
        if (splits == null) {
        	noCheckUrls.add(urlsStr);
        	return;
        }
        
        for (String split : splits) {
        	if (split.matches("/^\\s+$/")) continue;
        	if (split.matches("/^|$/")) continue;
        	
        	split = split.trim();
        	MyLogger.get().info("noCheckUrls:add one:"+split);
        	noCheckUrls.add(split);
        }
    }
    
    private boolean fromValidSession(HttpServletRequest request) {
    	TZSCCookieInfo cinfo = Api.getCookieInfo(request);
    	return cinfo.isValidSession();
    }
}

/**
 * 当检查到有非法会话访问时，更新攻击源信息。
 * 当某次攻击与上一次攻击的时间之差小于ATTACK_TIME_MAX_INTERVAL时，认为这两次攻击是连续攻击
 * 当攻击源连续攻击TOO_LONG_ATTACK_TIME长时间或者每秒遭到的攻击大于1次后，系统应该开始静默该用户5分钟。
 * 静默5分钟内若仍然收到非法会话攻击，
 * 	  则，静默攻击时间重新开始计时。
 *   否则，取消静默
 * @author zleil
 *
 */

class Attacker{
	public Attacker(String src) {
		this.src = src;
		this.firstAttackTime = System.currentTimeMillis();
		this.lastAttackTime = this.firstAttackTime;
		this.attackTimes = 1;
	}
	
	/**
	 * 收到“源”的一次有效会话请求，更新“源”的攻击数据
	 * @return true,需要删除攻击源对象，认为此“源”不再是攻击者
	 */
	public boolean succeed() {
		long cur = System.currentTimeMillis();
		if (isTooLongAttack()) {
			if ((cur - this.lastAttackTime) > REFUSE_TIME) {
				return true;
			} else {
				return false;
			}
		} else {
			if (cur - this.lastAttackTime > ATTACK_TIME_MAX_INTERVAL) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	/**
	 * 收到“源”的一次无效会话请求，更新“源”的攻击数据
	 */
	public void updateLastAttackTime() {
		long cur = System.currentTimeMillis();
		if (cur - this.lastAttackTime < ATTACK_TIME_MAX_INTERVAL) {
			this.lastAttackTime = cur;
			
			++this.attackTimes;
		} else {
			this.firstAttackTime = cur;
			this.lastAttackTime = this.firstAttackTime;
			this.attackTimes = 1;
		}
	}
	
	public boolean mustRefuse() {
		long cur = System.currentTimeMillis();
		return isTooLongAttack() && (cur - this.lastAttackTime) < REFUSE_TIME;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"src\":\"");
		sb.append(src);
		sb.append("\",\"lastAttackTime\":\"");
		sb.append(lastAttackTime);
		sb.append("\",\"firstAttackTime\":\"");
		sb.append(firstAttackTime);
		sb.append("\",\"attackTimes\":");
		sb.append(attackTimes);
		sb.append("}");
		
		return sb.toString();
	}
	
	private boolean isTooLongAttack() {
		long interval = this.lastAttackTime - this.firstAttackTime;
		if (interval == 0) return false;
		
		return (interval > TOO_LONG_ATTACK_TIME) ||
				((this.attackTimes*1000)/interval > ATTACK_TIMES_PER_SECOND);
	}
	
	public String src;
	public long lastAttackTime;
	public long firstAttackTime;
	public long attackTimes;
	
	public long ATTACK_TIME_MAX_INTERVAL = 50; //连续2次攻击的最大时间间隔，50ms
	public long TOO_LONG_ATTACK_TIME = 10*1000; //遭受过久持续攻击后需要静默攻击源，10s
	public long REFUSE_TIME = 30*1000; //静默时间，30s
	public long ATTACK_TIMES_PER_SECOND = 50; //最大每秒攻击次数，超过此次数，系统认为遭受持久攻击，此时需要静默攻击源
}

class AttackerMgr{
	static public synchronized AttackerMgr instance() {
		if (inst == null) {
			inst = new AttackerMgr();
		}
		
		return inst;
	}
	
	static private AttackerMgr inst = null;
	
	/**
	 * 每收到一次“源”的有效会话，则调用此方法
	 * @param src
	 */
	public synchronized void succeed(String src) {
		Attacker attacker = attackers.get(src);
		if (attacker == null) return;
		
		if (attacker.succeed()) {
			attackers.remove(src);
		}
	}
	
	/**
	 * 每收到一次“源”的无效会话，则调用此方法
	 * @param src
	 */
	public synchronized void updateLastAttackTime(String src) {
		Attacker attacker = attackers.get(src);
		if (attacker == null) {
			attacker = new Attacker(src);
			attackers.put(src, attacker);
			return;
		}
		
		attackers.get(src).updateLastAttackTime();
	}
	
	/**
	 * 判断是否需要对“源”做静默拒绝动作。
	 * @param src
	 * @return true：则要求拒绝对此请求做后续任何处理
	 */
	public synchronized boolean mustRefuse(String src) {
		Attacker attacker = attackers.get(src);
		if (attacker == null) {
			return false;
		}
		
		if (attacker.mustRefuse()) {
			MyLogger.getAttackLog().info("mustRefuse this attacker: "+attacker);
			return true;
		} else {
			return false;
		}
	}
	
	private AttackerMgr() {}
	
	private Map<String,Attacker> attackers = new HashMap<String,Attacker>();
}
