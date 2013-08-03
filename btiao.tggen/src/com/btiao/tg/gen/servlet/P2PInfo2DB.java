package com.btiao.tg.gen.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.tg.gen.service.P2PInfoMgr;

public class P2PInfo2DB extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6160819116981605014L;
	static public void main(String[] args) throws Exception {
		int i = (int)Integer.parseInt("1.1");
		System.out.println(i);
	}
	static volatile int process_num = 0;
	static synchronized void inc() {
		++ process_num;
	}
	static long t1 = 0;
	
	private boolean procMulti(Map<String,String[]> args) {
		boolean r = true;
		try {
			String city = args.get("city")[0];
			String[] splitP1x = args.get("p1x")[0].split(",");
			String[] splitP1y = args.get("p1y")[0].split(",");
			String[] splitP2x = args.get("p2x")[0].split(",");
			String[] splitP2y = args.get("p2y")[0].split(",");
			String[] splitDist = args.get("dist")[0].split(",");

			P2PInfoMgr.instance().toDBInit(city);
			
			for (int i=0; i<splitP1x.length; ++i) {
				int p1x = Integer.parseInt(splitP1x[i]);
				int p1y = Integer.parseInt(splitP1y[i]);
				int p2x = Integer.parseInt(splitP2x[i]);
				int p2y = Integer.parseInt(splitP2y[i]);
				int dist = (int)Float.parseFloat(splitDist[i]);

				boolean rOne = false;
				
				if (!args.get("dist_gj")[0].startsWith(".")) {
					String[] splitGJ = args.get("dist_gj")[0].split(",");
					String[] splitTime = args.get("time_gj")[0].split(",");
					int time = (int)Float.parseFloat(splitTime[i]);
					int dist_gj = (int)Float.parseFloat(splitGJ[i]);
					rOne = P2PInfoMgr.instance().toDBGJ(city,p1x,p1y,p2x,p2y,dist_gj, time, dist);
				} else if (!args.get("dist_zj")[0].startsWith(".")) {
					String[] splitZJ = args.get("dist_zj")[0].split(",");
					String[] splitTime = args.get("time_zj")[0].split(",");
					int time = (int)Float.parseFloat(splitTime[i]);
					int dist_zj = (int)Float.parseFloat(splitZJ[i]);
					rOne = P2PInfoMgr.instance().toDBZJ(city,p1x,p1y,p2x,p2y,dist_zj, time, dist);
				} else {
					rOne = P2PInfoMgr.instance().toDBDist(p1x,p1y,p2x,p2y,dist);
				}
				
				r = rOne ? r : false;
				if (!r) {
					break;
				}
			}
			
			P2PInfoMgr.instance().toDBCommit(city);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(args);
			r = false;
		}
		return r;
	}
	public void doGet (HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
		boolean result = true;
		
		{
			if (process_num == 0) {
				t1 = System.currentTimeMillis();
			}
			inc();
			long t2 = System.currentTimeMillis();
			System.out.println("speed:" + ((float)process_num*1000)/((float)(t2-t1)));
		}
		
		{
			Map<String,String[]> args = req.getParameterMap();
			String[] clearInc = args.get("clearInc");
			if (clearInc != null && clearInc.length > 0) {
				process_num = 0;
				t1 = System.currentTimeMillis();
				rsp(res, true);
				return;
			}
		}
		
		{
			Map<String,String[]> args = req.getParameterMap();
			String[] multi = args.get("multi");
			if (multi !=null && multi.length > 0) {
				int num = Integer.parseInt(multi[0]);
				process_num += num;
				
//				class My extends Thread {
//					public Map<String,String[]> args;
//					P2PInfo2DB pobj;
//					public void run() {
//						pobj.procMulti(args);
//					}
//				}
//				My my = new My();
//				my.args = args;
//				my.pobj = this;
//				my.start();
//				
//				result = true;
				long t1 = System.currentTimeMillis();
				result = procMulti(args);
				long t2 = System.currentTimeMillis();
				System.out.println("t2-t1="+(t2-t1));
				
				rsp(res, result);
				return;
			}
		}

		Map<String,String[]> args = req.getParameterMap();
		String city = args.get("city")[0];
		int p1x = (int)Float.parseFloat(args.get("p1x")[0]);
		int p1y = (int)Float.parseFloat(args.get("p1y")[0]);
		int p2x = (int)Float.parseFloat(args.get("p2x")[0]);
		int p2y = (int)Float.parseFloat(args.get("p2y")[0]);
		
		int dist = Integer.parseInt(args.get("dist")[0]);
		
		try {
			String[] dgjs = args.get("dist_gj");
			String[] dzjs = args.get("dist_zj");
			if (dgjs != null && dgjs.length >0) {
				int dgj = (int)Float.parseFloat(args.get("dist_gj")[0]);
				int tgj = (int)Float.parseFloat(args.get("time_gj")[0]);
				result = P2PInfoMgr.instance().toDBGJ(city, p1x,p1y,p2x,p2y, dgj, tgj, dist);
			} else if (dzjs != null && dzjs.length >0) {
				int dzj = (int)Float.parseFloat(args.get("dist_zj")[0]);
				int tzj = (int)Float.parseFloat(args.get("time_zj")[0]);
				P2PInfoMgr.instance().toDBZJ(city, p1x,p1y,p2x,p2y, dzj, tzj, dist);
			} else {
				P2PInfoMgr.instance().toDBDist(p1x,p1y,p2x,p2y, dist);
			}
		} catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		
		rsp(res, result);
	}
	
	void rsp(HttpServletResponse res, boolean result) throws ServletException, IOException {
		res.setContentType("text/html;charset=UTF-8");
		
		PrintWriter out = res.getWriter();
		if (result) {
			out.print("true");
		} else {
			out.print("false");
		}
	}
}
