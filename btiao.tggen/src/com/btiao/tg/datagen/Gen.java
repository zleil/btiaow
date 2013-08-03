package com.btiao.tg.datagen;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import com.btiao.tg.TgData;
import com.btiao.tg.TgShop;

public abstract class Gen {
	static public void main(String[] args) throws Exception {
		clearAllDB();
		
		List<Gen> gens = new ArrayList<Gen>();
		
		gens.add(new WoWoGen());
		gens.add(new MeiTuanGen());
		gens.add(new LaShouGen());
		gens.add(new NuoMiGen());
		CUT_UNKOWN_TYPE = true;
		
		Gen.setCity("beijing");
		Gen.initDB();
		Gen.genAll(gens);
		Gen.shutdownDB();
		
		try {
			assert(false);
			System.out.println("pls use -ea VM argument!");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}
	
	static public void clearAllDB() {
		File dir = new File(DBDIR);
		File[] files = dir.listFiles();
		if (files == null) {
			return;
		}
		
		for (File file : files) {
			file.delete();
		}
	}
	
	static public void genAll(List<Gen> gens) throws Exception {
		for (Gen gen : gens) {
			gen.init();
			gen.toDB();
		}
	}
	
	static public void shutdownDB() throws Exception {
		shutdownDB(tgCon);
	}
	
	static public void setCity(String acity) {
		city = acity;
	}
	
	/**
	 * 初始化DB
	 * @throws Exception
	 */
	static public void initDB() throws Exception {
		tgCon = DriverManager.getConnection("jdbc:hsqldb:file:"+DBDIR+File.separator+tgDBId+"."+city, "SA", "");
		
		try {
			Statement s = tgCon.createStatement();
			String sql = "CREATE TABLE tb_tg(" +
					"type INTEGER NOT NULL," +
					"longitude BIGINT NOT NULL," +
					"latitude BIGINT NOT NULL," +
					"dist INTEGER NOT NULL," +
					"shopName VARCHAR(64) NOT NULL," +
					"url VARCHAR(256) NOT NULL," +
					"title VARCHAR(512) NOT NULL," +
					"desc VARCHAR(512) NOT NULL," +
					"imageUrl VARCHAR(256) NOT NULL," +
					"startTime BIGINT NOT NULL," +
					"endTime BIGINT NOT NULL," +
					"useEndTime BIGINT NOT NULL," +
					"value INTEGER NOT NULL," +
					"price INTEGER NOT NULL," +
					"boughtNum INTEGER NOT NULL," +
					"PRIMARY KEY(longitude,latitude,url)" +
					")";
			s.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		try {
			Statement s = tgCon.createStatement();
			String sql = "CREATE TABLE tb_shop(" +
					"longitude BIGINT NOT NULL," +
					"latitude BIGINT NOT NULL," +
					"name VARCHAR(128) NOT NULL," +
					"addr VARCHAR(256) NOT NULL," +
					"tel VARCHAR(128) NOT NULL," +
					"PRIMARY KEY(longitude,latitude)" +
					")";
			s.execute(sql);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	static private void shutdownDB(Connection cn) throws Exception {
		cn.commit();
		Statement s = cn.createStatement();
		s.execute("SHUTDOWN COMPACT");
		s.close();
		cn.close();
	}
	
	static public boolean CUT_UNKOWN_TYPE = false;
	static public String DBDIR = "btdbdir";
	static public String ORIGIN_TG_DIR = "originTg"+File.separator+"origin";
	
	static public final String tgDBId = "tg";
	static public final String tgShopDBId= "tgshop";
	
	static protected Map<String,String> dbTgs = new HashMap<String,String>();
	static protected Map<String,String> dbShops = new HashMap<String,String>();
	
	static private Connection tgCon;
	
	static protected String city;

	/**
	 * 初始化Gen的静态数据。
	 */
	public void init() throws Exception {
		String originTgXmlFn = getTgXMLFn();
		SAXReader reader = new SAXReader();
		doc = reader.read(new File(ORIGIN_TG_DIR+File.separator+originTgXmlFn));
	}
	

	
	public void toDB() throws Exception {
		preGen();
		
		while (genTg()) {
			if (CUT_UNKOWN_TYPE && this.tgTmp.type == TgData.TgType.unkown) {
				continue; //未知类型的不便于搜索
			}
			
			if (alreadyAddedTg()) {
				continue;
			}
			
			insertTg();
			insertShop();
		}
		
		postGen();
	}
	
	protected long tmStr2Long(String tm) {
		try {
			SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = fm.parse(tm);
			
			return date.getTime();
		} catch (Exception e) {
			return -1;
		}
	}
	
	/**
	 * 价格字符串（小数），转换成整数，放大100倍。
	 * @param str
	 * @return
	 */
	protected Integer priceStr2Int(String str) {
		try {
			float r = Float.parseFloat(str);
			
			return (int)(r*100);
		} catch (Exception e) {
			return 0;
		}
	}
	
	protected Integer str2Int(String str) {
		try {
			int r = Integer.parseInt(str);
			
			return r;
		} catch (Exception e) {
			return null;
		}
	}
	
	protected long doubleLatLon2Long(String d) {
		try {
			double dlong = Double.parseDouble(d);
			return (long)(dlong*1000*1000);
		} catch (Exception e) {
			return (0xffffffffffffffffL);
		}
	}
	
	protected void insertTg() throws Exception {
		if (dbTgs.containsKey(tgTmp.url)) {
			return;
		}
		
		String sql = null;
		Statement s = null;
		try {
			s = tgCon.createStatement();
			for (TgShop shopTmp : shopsTmp) {
				sql = genInsertTgSql(tgTmp, shopTmp);
				s.execute(sql);
			}
			tgCon.commit();
			s.close();
		} catch (Exception e) {
			System.err.println("sql="+sql);
			e.printStackTrace();
		} finally {
			if (s != null) s.close();
		}
		
		dbTgs.put(tgTmp.url, "");
	}

	/**
	 * 每次调用产生一条团购信息，填写到tgTmp和shopsTmp中。
	 * @return true表示是否取到了下一条团购数据。
	 */
	protected abstract boolean genTg();
	
	/**
	 * 返回团购网站名称
	 * @return
	 */
	protected abstract String getName();
	
	protected abstract void preGen();
	protected abstract void postGen() throws Exception ;
	
	private String getTgXMLFn() {
		return getName()+"."+city+".xml";
	}
	
	private boolean alreadyAddedTg() {
		if (dbTgs.containsKey(tgTmp.url)) {
			return true;
		}
		else {
			return false;
		}
	}

	private String genInsertShopSql(TgShop shop) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO tb_shop ");
		sb.append("(longitude,latitude,addr,name,tel) VALUES(");
		sb.append(shop.longitude);sb.append(",");
		sb.append(shop.latitude);sb.append(",");
		sb.append(normalTxt2Sqltxt(shop.addr));sb.append(",");
		sb.append(normalTxt2Sqltxt(shop.name));sb.append(",");
		sb.append(normalTxt2Sqltxt(shop.tel));
		sb.append(")");
		
		return sb.toString();
	}
	
	private String normalTxt2Sqltxt(String str) {
		//str = "'\\%_/<>";
		return "'" + 
			str.replace("'", "''") +
			"\'";
	}
	
	
	private String genInsertTgSql(TgData tg, TgShop shop) {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO tb_tg ");
		sb.append("(type,longitude,latitude,dist,shopName,url,title,desc,imageUrl,startTime,endTime,useEndTime,value,price,boughtNum) VALUES(");
		sb.append(tg.type);sb.append(",");
		sb.append(shop.longitude);sb.append(",");
		sb.append(shop.latitude);sb.append(",");
		sb.append(tg.dist);sb.append(",");
		sb.append(normalTxt2Sqltxt(shop.name+"（"+shop.addr+"）"));sb.append(",");
		sb.append(normalTxt2Sqltxt(tg.url));sb.append(",");
		sb.append(normalTxt2Sqltxt(tg.title));sb.append(",");
		if (tg.desc.length() > 120) {
			tg.desc = tg.desc.substring(0, 120);
		}
		sb.append(normalTxt2Sqltxt(tg.desc));sb.append(",");
		sb.append(normalTxt2Sqltxt(tg.imageUrl));sb.append(",");
		sb.append(tg.startTime);sb.append(",");
		sb.append(tg.endTime);sb.append(",");
		sb.append(tg.useEndTime);sb.append(",");
		sb.append(tg.value);sb.append(",");
		sb.append(tg.price);sb.append(",");
		sb.append(tg.boughtNum);
		sb.append(")");
		
		return sb.toString();
	}
	
	private void insertShop() throws Exception {
		for (TgShop shop : shopsTmp) {
			String id = shop.longitude + "," +shop.latitude;
			if (dbShops.containsKey(id)) {
				String shopName = dbShops.get(id);
				if (!shopName.equals(shop.name)) {
					//TODO 记录便于查看
				}
				continue;
			}
			
			String sql = genInsertShopSql(shop);
			Statement s = tgCon.createStatement();
			s.execute(sql);
			
			dbShops.put(id, shop.name);
		}
	}
	
	protected Document doc;
	protected TgData tgTmp;
	protected List<TgShop> shopsTmp = new ArrayList<TgShop>();
}
