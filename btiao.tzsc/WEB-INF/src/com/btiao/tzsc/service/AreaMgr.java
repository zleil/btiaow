package com.btiao.tzsc.service;

import java.util.HashMap;

public class AreaMgr {
	static public synchronized AreaMgr instance() {
		if (inst == null) {
			inst = new AreaMgr();
		}
		return inst;
	}
	
	static public String persistFn = "areas";
	static private AreaMgr inst = null;
	
	public synchronized void add(Area area) {
		all.put(area.id, area);
		this.changed = true;
	}
	
	public synchronized Area get(long id) {
		return this.all.get(id);
	}
	
	private AreaMgr() {
		load();
		
		if (!all.containsKey(65537)) {
			Area area = new Area();
			area.id = 65537;
			area.desc = "望春园";
			area.wxId = "gh_3afc64ac9887";
			add(area);
		}
		
		PersistObj.addBackTask(new Runnable() {

			@Override
			public void run() {
				synchronized (AreaMgr.instance()) {
					if (!AreaMgr.instance().hasChanged()) {
						return;
					}
					
					new PersistObj().persist(persistFn, all);
					
					AreaMgr.instance().setUnchanged();
				}
			}
			
		});
	}
	
	@SuppressWarnings("unchecked")
	private void load() {
		Object persistAll = new PersistObj().load(AreaMgr.persistFn);
		if (persistAll == null) {
			MyLogger.get().warn("get persist areamgr failed!");
			return;
		}
		
		all = (HashMap<Long, Area>) persistAll;
	}
	
	private void setUnchanged() {
		changed = false;
	}
	
	private boolean hasChanged() {
		return changed;
	}
	
	private HashMap<Long,Area> all = new HashMap<Long,Area>();
	
	private volatile boolean changed = false;
}
