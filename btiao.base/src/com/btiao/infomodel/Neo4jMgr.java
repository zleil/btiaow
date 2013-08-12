package com.btiao.infomodel;

import java.io.File;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jMgr {
	static public synchronized Neo4jMgr instance() {
		if (inst == null) {
			inst = new Neo4jMgr();
		}
		return inst;
	}

	static public String DB_PATH = "gdb"+File.separator+"my";
	static private Neo4jMgr inst = null;
	
	public void init() {
		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
		
		Runtime.getRuntime().addShutdownHook( new Thread() {
			@Override
			public void run() {
				graphDb.shutdown();
			}
		});
	}
	
	public void clearDB() {
		clearFile(DB_PATH);
	}
	
	public void clearFile(String filename) {
		File dir = new File(filename);
		if (!dir.exists()) {
			return;
		}
		
		if (dir.isFile()) {
			dir.delete();
			return;
		}
		
		String[] fns = dir.list();
		if (fns != null) for (String fn : fns) {
			clearFile(filename+File.separator+fn);
		}
		
		dir.delete();
	}
	
	public GraphDatabaseService db() {
		return graphDb;
	}
	
	private Neo4jMgr(){}
	
	private GraphDatabaseService graphDb;
}
