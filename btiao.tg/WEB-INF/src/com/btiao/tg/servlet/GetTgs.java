package com.btiao.tg.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.tg.Result;
import com.btiao.tg.TgData;
import com.btiao.tg.domain.UserFilter;
import com.btiao.tg.service.AllTgMgr;

public class GetTgs extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6625752365429394531L;

	/**
	 * 输入：
	 * idx=123&pgs=123;
	 * 返回格式
	 * {result=123;desc="";more:true/false; tgs:[{tgObj}];}
	 */
	public void doGet (HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
		res.setContentType("text/html;charset=UTF-8");
		PrintWriter out = res.getWriter();
		
		int result = 0;
		Map<String,String[]> args = req.getParameterMap();
		
		int idx = 0;
		int pgs = 0;
		UserFilter f = new UserFilter();
		
		try {
			idx = getIntArg(args, "idx");
			pgs = getIntArg(args, "pgs");
			setUserFilter(args, f);
		} catch (BTiaoExp e) {
			result = e.errNo;
		}
		if (result != Result.SUCCESS) {
			outErr(out, result);
			return;
		}

		List<TgData> tgs = null;
		try {
			tgs = AllTgMgr.instance().getTg(f, idx, pgs);
			out.print("var rst = {result:0,tgs:[");
			
			StringBuilder sb = new StringBuilder();
			int num = tgs.size();
			if (num > 0) {
				Map<String,Boolean> maskAttr = new HashMap<String,Boolean>();
				maskAttr.put("url", true);
				maskAttr.put("imageUrl", true);
				maskAttr.put("title", true);
				maskAttr.put("price", true);
				maskAttr.put("value", true);
				maskAttr.put("dist", true);
				maskAttr.put("longitude", true);
				maskAttr.put("latitude", true);
				maskAttr.put("shopName", true);
				maskAttr.put("desc", true);
				
				JsonCvt.obj(tgs.get(0), sb, maskAttr);
				for (int i=1; i<num; ++i) {
					sb.append(",");
					JsonCvt.obj(tgs.get(i), sb, maskAttr);
			    }
			}
		    
			out.print(sb);
		    out.print("]}");
		} catch (BTiaoExp e) {
			outErr(out, e.errNo);
		}

	    out.close(); 
	}
	
	private void outErr(PrintWriter out, int errNo) {
		String str = "var rst = {result:"+errNo+",desc:\""+Result.desc(errNo)+"\"}";
		out.print(str);
	}
	
	private int getIntArg(Map<String,String[]> args, String name) throws BTiaoExp {
		int ret = 0;
		String[] retArg = args.get(name);
		if (retArg != null && retArg.length > 0) {
			try {
				ret = Integer.parseInt(retArg[0]);
				return ret;
			} catch (Exception e) {
				throw new BTiaoExp(Result.ARG_ERROR, e);
			}
		} else {
			throw new BTiaoExp(Result.ARG_ERROR, null);
		}
	}
	
	private long getLongArg(Map<String,String[]> args, String name) throws BTiaoExp {
		long ret = 0;
		String[] retArg = args.get(name);
		if (retArg != null && retArg.length > 0) {
			try {
				ret = Long.parseLong(retArg[0]);
				return ret;
			} catch (Exception e) {
				throw new BTiaoExp(Result.ARG_ERROR, e);
			}
		} else {
			throw new BTiaoExp(Result.ARG_ERROR, null);
		}
	}
	
	private void setUserFilter(Map<String,String[]> args, UserFilter f) throws BTiaoExp {
		String city = "";
		int uLongitude = -1;
		int uLatitude = -1;
		int tgType = -1;
				
		String[] filterArg = args.get("flt");
		if (filterArg != null && filterArg.length > 0) {
			f = new UserFilter();
			for (String filterStr : filterArg) {
				if (!f.addFilter(filterStr)) {
					throw new BTiaoExp(Result.ARG_ERROR, null);
				}
				continue;
			}
		}
		
		if (args.containsKey("tgType")) {
			tgType = getIntArg(args, "tgType");
			f.addFilter("type="+tgType);
		}
		
		city = getStrArg(args, "city");
		uLongitude = getIntArg(args, "uLon");
		uLatitude = getIntArg(args, "uLat");
		
		f.city = city;
		f.uLat = uLatitude;
		f.uLon = uLongitude;
	}
	
	private String getStrArg(Map<String,String[]> args, String name) throws BTiaoExp {
		String[] retArg = args.get(name);
		if (retArg != null && retArg.length > 0) {
			try {
				return retArg[0];
			} catch (Exception e) {
				throw new BTiaoExp(Result.ARG_ERROR, e);
			}
		} else {
			throw new BTiaoExp(Result.ARG_ERROR, null);
		}
	}
}
