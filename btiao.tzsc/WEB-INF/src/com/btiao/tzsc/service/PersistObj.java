package com.btiao.tzsc.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PersistObj {
	static private String persistDir = "tzscdb";
	
	static ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
	
	static {
		File dir = new File(persistDir);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}
	
	static void addBackTask(Runnable task) {
		service.scheduleAtFixedRate(
		        task, 10,
				30, TimeUnit.SECONDS);
	}
	
	static void addBackTask(final String fn) {
		addBackTask(new Runnable() {
			@Override
			public void run() {
				new PersistObj().moveAndBack(fn);
			}	
		});
	}
	
	public void persist(String fn, Object obj) {
		if (obj == null) {
			return;
		}
		
		//1. write to a tmp file
		FileOutputStream fout = null;
		ObjectOutputStream objOut = null;
		try {
			fout = new FileOutputStream(new File(persistDir+File.separator+fn+".tmp"));
			objOut = new ObjectOutputStream(fout);
			objOut.writeObject(obj);
		} catch (Throwable e) {
			MyLogger.get().warn("persist the file '" + fn + "' failed!", e);
		}finally {
			try {
				if (objOut != null) objOut.close();
				if (fout != null) fout.close();
			} catch (Throwable e) {
				MyLogger.get().warn("persist the file '" + fn + "' failed(in finally)!", e);
			}
		}
		
		//2. rename to the latest db file
		File file = new File(persistDir+File.separator+fn+".tmp");
		if (file.exists()) {
			file.renameTo(new File(persistDir+File.separator+fn));
		}
	}
	
	public void moveAndBack(String fn) {
		String dest = persistDir+File.separator+fn + "_" + System.currentTimeMillis();
		new File(fn).renameTo(new File(dest));
	}
	
	public Object load(String fn) {
		FileInputStream fin = null;
		ObjectInputStream objIn = null;
		try {
			File file = new File(persistDir+File.separator+fn);
			if (file.exists()) {
				fin = new FileInputStream(file);
				objIn = new ObjectInputStream(fin);
				@SuppressWarnings("unchecked")
				Object ret = objIn.readObject();
				
				return ret;
			}			
		} catch (Throwable e) {
			MyLogger.get().warn("load persist file '" + fn + "' failed!", e);
		}finally {
			try {
				if (objIn != null) objIn.close();
				if (fin != null) fin.close();
			} catch (Throwable e) {
				MyLogger.get().warn("load persist file '" + fn + "' failed(in finally)!", e);
			}
		}
		
		return null;
	}
}
