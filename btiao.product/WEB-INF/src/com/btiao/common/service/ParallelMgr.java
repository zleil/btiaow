package com.btiao.common.service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.btiao.infomodel.InfoMObject;

public class ParallelMgr {
	static public class ModelInfo {
		public InfoMObject ofNode;
		public Class<?extends InfoMObject> nodeClass;
		public String rightRelName;
		public String downRelName;
		
		public ModelInfo(InfoMObject ofN, Class<?extends InfoMObject> nc, String rr, String dr) {
			this.ofNode = ofN;
			this.nodeClass = nc;
			this.rightRelName = rr;
			this.downRelName = dr;
		}
		
		@Override
		public int hashCode() {
			return this.rightRelName.hashCode() + this.downRelName.hashCode() +
					this.ofNode.hashCode() + this.nodeClass.hashCode();
		}
		
		@Override
		public boolean equals(Object o) {
			ModelInfo m = (ModelInfo)o;
			return this.nodeClass == m.nodeClass &&
					this.rightRelName.equals(m.rightRelName) &&
					this.downRelName.equals(m.downRelName) &&
					this.ofNode.isSameObj(m.ofNode);
		}
		
		@Override
		public String toString() {
			return "ofNode=" + this.ofNode.toString() +
					",nodeClass=" + nodeClass.getName() +
					",rightRelName=" + this.rightRelName +
					",downRelName=" + this.downRelName;
		}
	}
	static private enum LockType {
		Write,
		Read
	}
	
	static private class MAInfo {
		/**
		 * model.
		 */
		@SuppressWarnings("unused")
		public ModelInfo m;
		
		/**
		 * current lock type.<br>
		 * read lock and write lock are mutual exclusive.<br>
		 * write lock are exclusive each other.<br> 
		 */
		public LockType lock;
		
		/**
		 * Map the owner thread to locked times.<br> 
		 */
		public Map<Thread,Integer> owners = new HashMap<Thread,Integer>();
		
		public MAInfo(ModelInfo m, LockType lock) {
			this.m = m;
			this.lock = lock;
		}
	}
	
	static public ParallelMgr instance() {
		if (inst == null) {
			inst = new ParallelMgr();
		}
		return inst;
	}
	static private ParallelMgr inst = null;
	
	static private final int read_wait_interval = 300;
	static private final int write_wait_interval = 100;
	
	/**
	 * attempt to get a read access to the specific model.
	 * @param m model identity
	 * @param t time to wait. 
	 *          <=0 is wait forever.
	 *          >0 unit is millisecond.
	 * @return true, can do read access.
	 *         false, time is out and haven't get a lock.
	 */
	public boolean readAccess(ModelInfo m, final long t) {
		long t2 = t;
		
		while (t<=0 || (t>0 && t2>0)) {
			synchronized (this) {
				if (isOwner(m) || isModelReadLocked(m)) {
					readLock(m);
					return true;
				} else {
					try {
						t2 -= read_wait_interval;
						this.wait(read_wait_interval);
					} catch (InterruptedException e) {}
				}
			}
		}
		
		return false;
	}
	
	public boolean writeAccess(ModelInfo m, final long t) {
		long t2 = t;
		
		while (t<=0 || (t>0 && t2>0)) {
			synchronized (this) {	
				if (isOwner(m) || !isModelLocked(m)) {
					writeLock(m);
					return true;
				} else {
					try {
						t2 -= write_wait_interval;
						this.wait(write_wait_interval);
					} catch (InterruptedException e) {}
				}
			}
		}
		
		return false;
	}
	
	public synchronized void release() {
		Thread th = Thread.currentThread();
		List<MAInfo> mas = thread2MAs.get(th);
		if (mas == null) {
			return;
		}
		
		//thread can relock one model, decrement until release all.
		MAInfo lastMA = mas.get(mas.size()-1);
		Integer times = lastMA.owners.get(th) - 1;
		if (times == 0) {
			lastMA.owners.remove(th);
		} else {
			lastMA.owners.put(th, times);
		}
		
		//if no thread lock the model, delete the MA.
		if (lastMA.owners.isEmpty()) {
			model2MA.remove(lastMA);
		}
		
		//remove the last MA locked by the thread
		mas.remove(mas.size()-1);
		
		//if current thread haven't locked any model, remove all related info.
		if (mas.size() == 0) {
			thread2MAs.remove(th);
		}
	}
	
	private boolean isOwner(ModelInfo m) {
		Thread th = Thread.currentThread();
		
		List<MAInfo> mas = thread2MAs.get(th);
		for (MAInfo ma : mas) {
			if (m.equals(ma)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * check if there is a write lock on the specific model.
	 * @param m
	 * @return
	 */
	private boolean isModelReadLocked(ModelInfo m) {
		MAInfo ma = model2MA.get(m);
		return ma != null && ma.lock == LockType.Write;
	}
	
	/**
	 * check if there is a lock on the specific model.
	 * @param m
	 * @return
	 */
	private boolean isModelLocked(ModelInfo m) {
		return model2MA.containsKey(m);
	}
	
	/**
	 * put a read lock.
	 * @param m
	 */
	private void readLock(ModelInfo m) {
		MAInfo ma = createLockOnModel(m, LockType.Read);
		setRelOfThread(ma);
	}
	
	private void writeLock(ModelInfo m) {
		MAInfo ma = createLockOnModel(m, LockType.Write);
		setRelOfThread(ma);
	}
	
	private void setRelOfThread(MAInfo ma) {		
		Thread th = Thread.currentThread();

		Integer times = ma.owners.get(th);
		if (times == null) {
			ma.owners.put(th, 1);
		} else {
			ma.owners.put(th, times+1);
		}
		
		List<MAInfo> mas = thread2MAs.get(th);
		if (mas == null) {
			mas = new LinkedList<MAInfo>();
			thread2MAs.put(th, mas);
		}
		
		mas.add(ma);
	}
	
	private MAInfo createLockOnModel(ModelInfo m, LockType lck) {
		MAInfo ma = model2MA.get(m);
		if (ma == null) {
			ma = new MAInfo(m, lck);
			model2MA.put(m, ma);
		}
		
		return ma;
	}
	
	/**
	 * Map a model to MA.<br>
	 * If a model has a entry in this data, then the model must have been <br>
	 * locked by a thread.<br>
	 */
	private Map<ModelInfo,MAInfo> model2MA = new HashMap<ModelInfo,MAInfo>();
	
	/**
	 * Map thread to all its owned MAs.<br>
	 * Thread can relock a model, one lock a model, no mater lock or relock,<br>
	 * there will be a MA add the MAs.<br>
	 */
	private Map<Thread,List<MAInfo>> thread2MAs = new HashMap<Thread,List<MAInfo>>();
}
