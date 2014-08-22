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
import com.btiao.tzsc.service.Tip;

public class TZSCFilter implements Filter {
	
	private List<String> noCheckUrls;  

    @Override  
    public void destroy() {  
        this.noCheckUrls = null;
    }  
  
    @Override  
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,  
        ServletException {
    	
    	MyLogger.get().warn("do a filter");
    	try {
    		HttpServletRequest httpRequest = (HttpServletRequest)request;  
            HttpServletResponse httpResponse = (HttpServletResponse)response;
            
    		String uri = httpRequest.getRequestURI();
    		for (String nocheckurl : noCheckUrls) {
    			if (uri.contains(nocheckurl)) {
    				MyLogger.get().error("here: get a nochecurl:"+nocheckurl+":uri="+uri);
    				chain.doFilter(request, response);
    				return;
    			}
    		}
    		
    		if (fromValidSession(httpRequest)) {
    			MyLogger.get().error("from a valid session");
    			chain.doFilter(request, response);
    			return;
    		} else {
    			MyLogger.get().error("from a invalid session");
    			String newUrl = "../webs/htmlconfirmtip.jsp?tip="+Tip.get().busy+"&hasyes=";
    			httpResponse.sendRedirect(newUrl);
    		}
    	} catch (Throwable e) {
    		MyLogger.getAttackLog().error("do filter failed!", e);
    		String newUrl = "../webs/htmlconfirmtip.jsp?tip="+Tip.get().busy+"&hasyes=";
			((HttpServletResponse)response).sendRedirect(newUrl);
    	}
    }  
  
    @Override  
    public void init(FilterConfig config) throws ServletException {   
        String urlsStr = config.getInitParameter("noCheckUrls");
        MyLogger.get().error("noCheckUrls="+urlsStr);
        
        String[] splits = urlsStr.split("|");
        if (splits == null) {
        	noCheckUrls.add(urlsStr);
        	return;
        }
        
        MyLogger.get().error("l:"+noCheckUrls.size());
        
        for (String split : splits) {
        	if (split.matches("/^\\s*$/")) continue;
        	if (split.matches("/^|$/")) continue;
        	
        	MyLogger.get().error("noCheckUrls:add one:"+split.trim());
        	noCheckUrls.add(split.trim());
        }
    }
    
    private boolean fromValidSession(HttpServletRequest request) {
    	TZSCCookieInfo cinfo = Api.getCookieInfo(request);
    	return cinfo.isValidSession();
    }
}
