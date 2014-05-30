package com.btiao.tzsc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WXAction extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3142857989535992028L;
	
	@Override
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) {	
		try {
			_doPost(request, response);
		} catch (Throwable e) {
			e.printStackTrace();
			
			PrintWriter out;
			try {
				out = response.getWriter();
				out.print("unknow error in WXAction doPost!");
			} catch (IOException ee) {
				e.printStackTrace();
			}
		}
	}
	
	public void _doPost(HttpServletRequest request,
			HttpServletResponse response) {	
		try {
			BufferedReader reader = request.getReader();
			
			StringBuilder sb = new StringBuilder();
			do {
				String tmp = reader.readLine();
				if (tmp == null) {
					break;
				}
				
				sb.append(tmp);
			} while (true);
			
			WXMsg msg = WXMsgFactory.gen(sb.toString());
			
			new WXMsgProcessor().proc(msg, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request,
			HttpServletResponse response) {	
		try {
			_doGet(request, response);
		} catch (Throwable e) {
			e.printStackTrace();
			
			PrintWriter out;
			try {
				out = response.getWriter();
				out.print("unknow error in WXAction doGet!");
			} catch (IOException ee) {
				e.printStackTrace();
			}
		}
	}

	public void _doGet(HttpServletRequest request,
			HttpServletResponse response) {		
		String sig = request.getParameter("signature");
		String tm = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		
		String echostr = request.getParameter("echostr");
		String token = "cqzytsyrjnnjdwiloveurebecca";
		
		StringBuilder sb = new StringBuilder();
		String[] beSort = {tm, nonce, token};
		Arrays.sort(beSort);
		sb.append(beSort[0]);
		sb.append(beSort[1]);
		sb.append(beSort[2]);
		
		System.out.println("signature="+sig);
		System.out.println("timestamp="+tm);
		System.out.println("nonce="+nonce);
		System.out.println("echostr="+echostr);
		
		System.out.println("tosha1="+sb.toString());
		
		String sha1str = Sha1.doit(sb.toString());
		
		System.out.println("sha1="+sha1str);
		
		PrintWriter out;
		try {
			out = response.getWriter();
			
			if (sig.equals(sha1str)) {
				out.print(echostr);
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}
