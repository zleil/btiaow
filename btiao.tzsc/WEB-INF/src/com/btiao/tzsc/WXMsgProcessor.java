package com.btiao.tzsc;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.btiao.tzsc.WXMsg.Text;

public class WXMsgProcessor {
	public void proc(WXMsg msg, HttpServletResponse rsp) throws Exception {
		if (msg instanceof WXMsg.Text) {
			processTextMsg((Text) msg, rsp.getOutputStream());
		}
	}
	
	private void processTextMsg(WXMsg.Text msg, OutputStream out) throws Exception  {
		
	}
}
