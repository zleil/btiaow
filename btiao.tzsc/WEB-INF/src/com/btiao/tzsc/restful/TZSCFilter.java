package com.btiao.tzsc.restful;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
    	
    	String busyUrlEncode = URLEncoder.encode(Tip.get().busy, "UTF-8");
    	try {
    		HttpServletRequest httpRequest = (HttpServletRequest)request;  
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            
    		String uri = httpRequest.getRequestURI();
    		for (String nocheckurl : noCheckUrls) {
    			if (uri.contains(nocheckurl)) {
    				MyLogger.get().info("here: get a nochecurl:"+nocheckurl+":uri="+uri);
    				chain.doFilter(request, response);
    				return;
    			}
    		}
    		
    		if (fromValidSession(httpRequest)) {
    			MyLogger.get().info("from a valid session:uri="+uri);
    			chain.doFilter(request, response);
    			return;
    		} else {
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
