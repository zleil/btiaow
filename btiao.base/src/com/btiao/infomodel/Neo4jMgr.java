package com.btiao.infomodel;

import java.io.File;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class Neo4jMgr {
	static public void main(String[] args) {
		Neo4jMgr.instance().clearDB();
		Neo4jMgr.instance().init();		
		GraphDatabaseService db = Neo4jMgr.instance().db();
		Node n1 = null;
		Node n2 = null;
		RelationshipType r = null;
		
		Transaction tx = db.beginTx();
		try {
			n1 = db.createNode();
			n2 = db.createNode();
			r = new RelationshipType() {
				public String name() {
					return "myrel";
				}
			};
			n1.createRelationshipTo(n2, r);
			
			tx.success();
		} finally {
			tx.finish();
		}
		
		assert(n1.getRelationships(r, Direction.OUTGOING).iterator().next() != null);
		
		tx = db.beginTx();
		try {
			n1.getRelationships(r, Direction.OUTGOING).iterator().next().delete();
			
			tx.success();
		} finally {
			tx.finish();
		}
		
		assert(!n1.getRelationships(r, Direction.OUTGOING).iterator().hasNext());
		
		try {
			assert(false);
			System.out.println("please use VM argument -ea");
		} catch (Throwable e) {
			System.out.println("success!");
		}
	}
	
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
