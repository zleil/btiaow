package com.btiao.tzsc.restful;

import java.io.IOException;
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
import com.btiao.tzsc.weixin.Tip;

public class TZSCFilter implements Filter {
	private List<String> noCheckUrls;  

    @Override  
    public void destroy() {  
        this.noCheckUrls = null;  
    }  
  
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,  
        ServletException {
    	HttpServletRequest httpRequest = (HttpServletRequest)request;  
        HttpServletResponse httpResponse = (HttpServletResponse)response; 
    	try {
    		String uri = httpRequest.getRequestURI();
    		for (String nocheckurl : noCheckUrls) {
    			if (uri.matches(nocheckurl)) {
    				MyLogger.get().info("here: get a nochecurl:"+nocheckurl+":uri="+uri);
    				chain.doFilter(request, response);
    				return;
    			}
    		}
    		
    		if (fromValidSession(httpRequest)) {
    			chain.doFilter(request, response);
    			return;
    		} else {
    			String newUrl = "../webs/htmlconfirmtip.jsp?tip="+Tip.get().busy+"&hasyes=";
    			httpResponse.sendRedirect(newUrl);
    		}
    	} catch (Throwable e) {
    		MyLogger.getAttackLog().warn("do filter failed!", e);
    		httpResponse.sendRedirect("login.jsp");
    	}
    }  
  
    @Override  
    public void init(FilterConfig config) throws ServletException {   
        String urlsStr = config.getInitParameter("noCheckUrls");
        String[] splits = urlsStr.split("|");
        if (splits == null) {
        	return;
        }
        
        for (String split : splits) {
        	noCheckUrls.add(split);
        }
    }
    
    private boolean fromValidSession(HttpServletRequest request) {
    	TZSCCookieInfo cinfo = Api.getCookieInfo(request);
    	return cinfo.isValidSession();
    }
}
