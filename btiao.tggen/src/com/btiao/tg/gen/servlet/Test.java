package com.btiao.tg.gen.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Test extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6622733955497445503L;

	static public void main(String[] args) throws Exception {
		String DBDIR = ".."+File.separator+"TgDataGen"+File.separator+"genTest"+File.separator+"db";
		String tgDBId = "tgdb";
		Connection cn = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+tgDBId+";ifexist=true", "SA", "");
		Statement s = cn.createStatement();
		String sql = "select * from tb_tg";

		ResultSet rst = s.executeQuery(sql);
		ResultSetMetaData meta = rst.getMetaData();
		int num = meta.getColumnCount();
		while (rst.next()) {
			for (int i=1; i<=num; ++i) {
				Object v = rst.getString(i);
				String col = meta.getColumnName(i);
				System.out.println(col+"="+v+",");
			}
		}
		
		s.close();
		cn.close();
		
	}

	public void doGet (HttpServletRequest req, HttpServletResponse res)
    throws ServletException, IOException {
	    PrintWriter out = res.getWriter();
	    out.println("hello, biantiao.org!");
	    
	    out.close(); 
	}
}
