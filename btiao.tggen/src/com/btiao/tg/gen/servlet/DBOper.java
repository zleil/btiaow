package com.btiao.tg.gen.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.tg.gen.service.P2PInfoMgr;


public class DBOper extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5589029591678194814L;

	/**
	 * op=init or close
	 * city=
	 */
	public void doGet (HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
		res.setContentType("text/html;charset=UTF-8");
		Map<String,String[]> args = req.getParameterMap();
		
		int result = 0;
		try {
			String[] handleArg = args.get("op");
			if (handleArg[0].equals("init")) {
				P2PInfoMgr.instance().init(args.get("city")[0]);
			} else if (handleArg[0].equals("close")) {
				P2PInfoMgr.instance().closeDB(args.get("city")[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = 1;
		}
		
		PrintWriter out = res.getWriter();
		if (result !=0) {
			out.append("false");
		} else {
			out.append("true");
		}
	}
}
