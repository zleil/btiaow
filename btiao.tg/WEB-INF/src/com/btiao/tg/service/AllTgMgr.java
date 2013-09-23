package com.btiao.tg.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.tg.Result;
import com.btiao.tg.TgData;
import com.btiao.tg.domain.UserFilter;
import com.btiao.tg.domain.UserFilter.Item;

public class AllTgMgr {
	static public class FilterRst {
		public ResultSet rst;
	}
	static public void main(String[] args) throws Exception {
		UserFilter f = new UserFilter();
		f.addFilter("longitude=116276000;latitude=39957000");
		f.city = "beijing";
		f.uLon = 116255000;
		f.uLat = 39808000;
		
		String fstr = "{city=beijing,lon=116255000,lat=39808000{{longitude=116276000,latitude=39957000,},}}";
		assert (f.toString().equals(fstr));
		
		String srcFn = "debug"+File.separator+"a.txt";
		File srcFile = new File(srcFn);
		if (!srcFile.exists()) {
			srcFile.createNewFile();
		}
		String dstFn = "debug"+File.separator+"b.txt";
		File dstFile = new File(dstFn);
		if (dstFile.exists()) {
			dstFile.delete();
		}
		assert(AllTgMgr.instance().exeCpyCmd(srcFn, dstFn)!=null);
		
		String tomcatdir = "D:\\dev\\apache-tomcat";
		DBDIR = tomcatdir + File.separator + "btdb";
		DBFDIR = "debug" + File.separator + "btfdb";
		PosDBMgr.DBDIR = tomcatdir + File.separator + "posdb";
		
		class ClearDBDir {
			void p(String fn) {
				File dir = new File(fn);
				File[] fs = dir.listFiles();
				if (fs != null) for (File f : fs) {
					f.delete();
				}
			}
		}
		new ClearDBDir().p(DBFDIR);
		
		
		AllTgMgr.instance().getTg(f, 0, 10);
		//System.out.println(container.size());

		try {
			assert(false);
			System.out.println("pls use -ea VM argument!");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}
	static synchronized public AllTgMgr instance() {
		if (inst == null) {
			inst = new AllTgMgr();
		}
		return inst;
	}
	static {
		try { 
			Class.forName("org.hsqldb.jdbcDriver");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public String DBDIR = "btdb";
	static public String DBFDIR = "btfdb";
	static public String tgDBId = "tg";
	
	static private volatile AllTgMgr inst = null;
	
	/**
	 * 返回handle对应的团购序列的一页数据。<br>
	 * 若没有对应过滤条件缓存DB，则新建一个。<br>
	 * 若有，则直接使用。<br>
	 * @param f 过滤条件
	 * @param idx 要获取的数据的起始索引
	 * @param pgs 要获取的数据的个数
	 * @return
	 */
	public List<TgData> getTg(UserFilter f, int idx, int num) 
	throws BTiaoExp 
	{
		boolean inRange = PosDBMgr.instance().judgeUserPos(f);
		
		if (inRange) {
			Boolean dbExist = fltdb.get(f.toString());
			if (dbExist == null || !((boolean)dbExist)) {
				genFilterDB(f);
				fltdb.put(f.toString(), true);
			}
		}
		
		List<TgData> r = getTgBlockData(inRange, f, idx, num);
		return r;
	}
	
	private AllTgMgr() {
		initExistFilterDB();
	}
	
	private void initExistFilterDB() {
		File fdbdir = new File(DBFDIR);
		if (!fdbdir.exists()) {
			fdbdir.mkdir();
			return;
		}
		
		String[] dbfns = fdbdir.list();
		for (String fn : dbfns) {
			if (!fn.endsWith(".properties")) {
				continue;
			}
			
			String dbId = fn.substring(0, fn.length()-".properties".length());
			String fstr = getFilterStrFromDBID(dbId);
			fltdb.put(fstr, true);
		}
	}
	
	private List<TgData> getTgBlockData(boolean inRange, UserFilter f, int idx, int num) {
		if (idx < 0) {
			idx = 0;
		}
		if (num < 0) {
			num = 0;
		}
		
		List<TgData> tgs = new ArrayList<TgData>();
		
		
		try {
			Connection cn = null;
			String options = ";readonly=true";
			if (inRange) {
				String dbId= genTgFilterDBOnlyFile(f, false);
				cn = DriverManager.getConnection("jdbc:hsqldb:file:"+DBFDIR+File.separator+dbId+options, "SA", "");
			} else {
				String dbId = getTgDBID(f.city);
				cn = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+dbId+options, "SA", "");
			}
			
			Statement s = cn.createStatement();
			String sql = "select * from tb_tg " + 
					genTgWhere(f) + genTgSort(f);
	
			ResultSet rst = s.executeQuery(sql);
			int begin = 0;
			while (rst.next()) {
				if (begin != idx) {
					++begin;
					continue;
				}
				
				if (num <= 0) break;
				
				TgData tg = new TgData();
				DBCvt.row2obj(rst, tg, null);
				tgs.add(tg);
				
				--num;
			}
			
			s.close();
			cn.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tgs;
	}
	
	private void genFilterDB(UserFilter f) throws BTiaoExp {
		String dbId = genTgFilterDBOnlyFile(f, true);
		
		if (dbId == null) {
			throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, null);
		}

		Connection cn = null;
		try {
			cn = DriverManager.getConnection("jdbc:hsqldb:file:"+DBFDIR+File.separator+dbId+";ifexist=true;shutdown=true", "SA", "");
			
			genTgDynamicData(cn, f);
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, e);
		} finally {
			if (cn != null) {
				try {
					cn.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void genTgDynamicData(Connection cn, UserFilter f) 
	throws BTiaoExp {
		List<TgData> tgs = new ArrayList<TgData>();
		
		try {
			Statement s = cn.createStatement();
			String sql = "select * from tb_tg " + genTgWhere(f);

			ResultSet rst = s.executeQuery(sql);
			while (rst.next()) {
				TgData tg = new TgData();
				
				Map<String,Boolean> mask = new HashMap<String,Boolean>();
				//mask.put("dist", null);
				DBCvt.row2obj(rst, tg, mask);
				tgs.add(tg);
			}
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_FDB_DYNAMIC_FAILED, e);
		}
		
		Connection cnp2p = null;
		try {
			cn.setAutoCommit(false);
			Statement us = cn.createStatement();
			
			cnp2p = getPosDBCon(f);
			Statement s = cnp2p.createStatement();
			
			int find_pos_failed_num = 0;
			for (TgData tg : tgs) {
				String sql = "select * from tb_p2pinfo_dist " + genPosWhere(tg, f);
				ResultSet rst = s.executeQuery(sql);
				if (rst.next()) {
					tg.dist = rst.getInt("dist");
					
					sql = "update tb_tg SET dist = " + tg.dist +
							" WHERE LONGITUDE=" + tg.longitude +
							" AND LATITUDE=" + tg.latitude;
					int r = us.executeUpdate(sql);
					if (r < 1) {
						System.err.println("update failed! sql="+sql);
					} else {
						cn.commit();
					}
				} else {
					++ find_pos_failed_num;
					System.err.println("find pos failed! sql="+sql);
				}
			}
			System.err.println("total find_pos_failed_num="+find_pos_failed_num+" in "+tgs.size());
			
			cn.commit();
			
			s.close();
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_FDB_DYNAMIC_FAILED, e);
		} finally {
			try {
				cnp2p.createStatement().execute("SHUTDOWN");
				if (cnp2p != null) cnp2p.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Connection getPosDBCon(UserFilter f) throws BTiaoExp {
		String dbId = PosDBMgr.instance().getDBId(f);

		try {
			Connection cn = DriverManager.getConnection("jdbc:hsqldb:file:"+PosDBMgr.DBDIR+File.separator+dbId+";ifexists=true", "SA", "");
			return cn;
		} catch (Exception e) {
			throw new BTiaoExp(Result.TG_GEN_POSDB_OPEN_FAILED, e);
		}
	}
	
	private String genPosWhere(TgData tg, UserFilter f) {
		UserFilter tmp = new UserFilter();
		tmp.uLat = tg.latitude;
		tmp.uLon = tg.longitude;
		PosDBMgr.instance().judgeUserPos(tmp);
		
		StringBuilder sb = new StringBuilder();
		sb.append(" WHERE " +
				"p1x=" + f.uLon + " AND " +
				"p1y=" + f.uLat + " AND " +
				"p2x=" + tmp.uLon + " AND " +
				"p2y=" + tmp.uLat);
		return sb.toString();
	}
	
	private String getDBIDByFilter(UserFilter f) {
		return "tg."+f.toString();
	}
	
	private String genTgFilterDBOnlyFile(UserFilter f, boolean newDB) {
		String dbId = getDBIDByFilter(f);

		if (newDB) {
			String fromDBFileId = DBDIR + File.separator + getTgDBID(f.city);
			String toDBFileId = DBFDIR + File.separator + dbId;
			
			try {
				copyTgDBFile(fromDBFileId, toDBFileId);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return dbId;
	}
	
	private Process exeCpyCmd(String sFn, String dFn) throws Exception {
		File f = new File(sFn);
		if (!f.exists()) {
			return null;
		}
		
		String[] cmd = new String[] {"cmd", "/C", "copy \""+sFn+"\" \""+dFn+"\""};
		return Runtime.getRuntime().exec(cmd, null, null);
	}
	
	private void copyTgDBFile(String fromDBFileId, String toDBFileId) 
	throws BTiaoExp {
		List<Process> ps = new ArrayList<Process>();
		try {
			Process p = exeCpyCmd(fromDBFileId+".script", toDBFileId+".script");
			if (p != null) ps.add(p);
			
			p = exeCpyCmd(fromDBFileId+".properties", toDBFileId+".properties");
			if (p != null) ps.add(p);
			
			p = exeCpyCmd(fromDBFileId+".data", toDBFileId+".data");
			if (p != null) ps.add(p);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, e);
		}
		
		do {
			try {
				int err = 0;
				for (Process p : ps) {
					int specialErr = p.waitFor();
					if (err == 0 && specialErr != 0) {
						try {
							int num = p.getErrorStream().available();
							byte[] b = new byte[num];
							p.getErrorStream().read(b);
							System.out.print(new String(b));
						} catch (Exception e) {
							e.printStackTrace();
						}
						err = specialErr;
						break;
					}
				}
				
				if (err != 0) {
					throw new BTiaoExp(Result.TG_GEN_FDB_FAILED, null);
				}
				break;
			} catch (InterruptedException e) {
				continue;
			}
		} while (true);
	}
	
	private String getTgDBID(String city) {
		return tgDBId + "." + city;
	}
	
	private String getFilterStrFromDBID(String dbId) {
		return dbId.substring(tgDBId.length()+1); //'.'分割符号还占用1个字符
	}

	private String genTgSort(UserFilter f) {
		return "ORDER BY dist"; //TODO 带实现其他排序
	}
	
	private String genTgWhere(UserFilter filter) {
		StringBuilder sb = new StringBuilder();
		boolean hasOneCond = false;
		
		sb.append("WHERE ");
		for (int i=0; i<filter.fs.size(); ++i) {
			List<Item> f = filter.fs.get(i);
			
			if (i != 0) {
				sb.append(" OR ");
			}
			
			sb.append("(");
			for (int j=0; j<f.size(); ++j) {
				if (j == 0) {
					sb.append(" ");
				} else {
					sb.append(" AND ");
				}
				Item it = f.get(j);
				sb.append(it.n);
				sb.append(it.op);
				sb.append(it.v);
				
				hasOneCond = true;
			}
			sb.append(")");
		}
		
		return hasOneCond ? sb.toString() : "";
	}
	
	/**
	 * UserFilter.toString -> true/false的映射，<br>
	 * 说明是否存在此过滤条件的缓存DB <br>
	 */
	private Map<String,Boolean> fltdb = new HashMap<String,Boolean>();
}
