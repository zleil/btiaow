package com.btiao.tg.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.btiao.tg.domain.UserFilter;

public class PosDBMgr {
	static class PosRange {
		int leftBottomLon;
		int leftBottomLat;
		
		int rightUpLon;
		int rightUpLat;
		
		int stepLon; //左下到右上的经度上的步长
		int stepLat; //左下到右上的纬度上的步长

		boolean isIn(long lon, long lat) {
			return (leftBottomLon <= lon && lon < rightUpLon) && 
					(leftBottomLat <= lat && lat < rightUpLat);
		}
		
		int judgeLat(int lat) {
			int num = (lat-leftBottomLat)/stepLat;
			return leftBottomLat + (num * stepLat);
		}
		
		int judgeLon(int lon) {
			int num = (lon-leftBottomLon)/stepLon;
			return leftBottomLon + (num * stepLon);
		}
	}
	
	static private PosDBMgr inst = null;
	static public String DBDIR = "posdb";
	
	static PosDBMgr instance() {
		if (inst == null) {
			inst = new PosDBMgr();
		}
		
		return inst;
	}
	
	static public void main(String[] args) {
		String dbId = "pos.beijing,116250000,39800000,5870,4506,60,78,10,20";
		//System.out.println(dbId.split("\\.").length);
		
		PosDBMgr.instance().genPosRangeFromDBID(dbId);
		assert(PosDBMgr.instance().posDBMap.size() == 1);
		
		try {
			assert(false);
			System.out.println("pls use -ea VM argument!");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}

	public boolean judgeUserPos(UserFilter f) {
		int lon = f.uLon;
		int lat = f.uLat;
		boolean matched = false;
		
		Set<Entry<PosRange,String>> entrys = posDBMap.entrySet();
		for (Entry<PosRange,String> entry : entrys) {
			PosRange range = entry.getKey();
			if (range.isIn(lon, lat)) {
				f.uLat = range.judgeLat(f.uLat);
				f.uLon = range.judgeLon(f.uLon);
				
				matched = true;
			}
//			
//			for (List<Item> items : f.fs) {
//				for (UserFilter.Item item : items) {
//					String name = item.n;
//					
//					if (name.equals("longitude") ||
//						name.equals("latitude")) {
//						int v = Integer.parseInt(item.v);
//						item.v = Integer.toString(range.judgeLon(v));
//					}
//					
//					if (name.equals("p1y") ||
//						name.equals("p2y")) {
//						int v = Integer.parseInt(item.v);
//						item.v = Integer.toString(range.judgeLat(v));
//					}
//				}
//			}
		}
		
		return matched;
	}
	public String getDBId(UserFilter f) {
		long lon = f.uLon;
		long lat = f.uLat;
		
		Set<Entry<PosRange,String>> entrys = posDBMap.entrySet();
		for (Entry<PosRange,String> entry : entrys) {
			PosRange range = entry.getKey();
			if (range.isIn(lon, lat)) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	private PosDBMgr() {
		init();
	}
	
	private void init() {
		File posDir = new File(DBDIR);
		File[] posDBFiles = posDir.listFiles();
		if (posDBFiles != null) for (File f : posDBFiles) {
			String fn = f.getName();
			String suffix = ".properties";
			
			int endIdx = fn.indexOf(suffix);
			if (endIdx == -1) {
				continue;
			} else {
				String dbId = fn.substring(0, endIdx);
				genPosRangeFromDBID(dbId);
			}
		}
	}
	
	private void genPosRangeFromDBID(String dbId) {
		PosRange range = new PosRange();
		
		String[] splitDbId1 = dbId.split("\\.");
		if (splitDbId1 == null || splitDbId1.length != 2) {
			System.err.println("found error dbId : " + dbId);
			return;
		}
		
		String[] splitDbId2 = splitDbId1[1].split(",");
		if (splitDbId2 == null || splitDbId2.length != 9) {
			System.err.println("found error dbId : " + dbId);
			return;
		}
		
		//String city = splitDbId2[0];
		
		int stepX = Integer.parseInt(splitDbId2[3]);
		int stepY = Integer.parseInt(splitDbId2[4]);
		//int numX = Integer.parseInt(splitDbId2[5]);
		int numY = Integer.parseInt(splitDbId2[6]);
		int startX = Integer.parseInt(splitDbId2[7]);
		int endX = Integer.parseInt(splitDbId2[8]);
		
		int originLon = Integer.parseInt(splitDbId2[1]);
		int originLat = Integer.parseInt(splitDbId2[2]);
		
		range.leftBottomLon = originLon + stepX*startX;
		range.leftBottomLat = originLat;
		
		range.rightUpLon = originLon + stepX*endX;
		range.rightUpLat = originLat + stepY*numY;
		
		range.stepLon = stepX;
		range.stepLat = stepY;
		
		posDBMap.put(range, dbId);
	}
	
	private Map<PosRange,String> posDBMap = new HashMap<PosRange,String>();
}
