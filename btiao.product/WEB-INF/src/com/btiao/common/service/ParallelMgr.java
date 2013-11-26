package com.btiao.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;

import com.btiao.base.model.BTiaoRoot;
import com.btiao.base.utils.BTiaoLog;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.Position;

public class ParallelMgr {
	public static void main(String[] args) {
		final ParallelMgr mgr = ParallelMgr.instance();
		final ModelInfo m = new ModelInfo(new BTiaoRoot(), Position.class, "", "");
		
		Object handle = mgr.writeAccess(m, 2000);
		assert(mgr.inst.model2MAList.size() == 1);
		mgr.release(handle);
		assert(mgr.inst.model2MAList.size() == 0);
		
		handle = mgr.writeAccess(m, 2000);
		new Thread() {
			public void run(){
				Object hd = mgr.writeAccess(m, 1000);
				assert(hd == null);
			}
		}.start();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mgr.release(handle);
		
		new Thread() {
			public void run(){
				Object hd = mgr.writeAccess(m, 1000);
				assert(hd != null);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mgr.release(hd);
			}
		}.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handle = mgr.readAccess(m, 1000);
		assert(handle == null);
		handle = mgr.writeAccess(m, 1000);
		assert(handle == null);
		
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		handle = mgr.readAccess(m, 1000);
		assert(handle != null);
		
		try {
			assert(false);
			System.out.println("set -ea argument to run!");
		}catch (Throwable e) {
			System.out.println("success!");
		}
	}
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
					this.ofNode.equals(m.ofNode);
		}
		
		@Override
		public String toString() {
			return "{ofNode=" + this.ofNode.toString() +
					",nodeClass=" + nodeClass.getName() +
					",rightRelName=" + this.rightRelName +
					",downRelName=" + this.downRelName + "}";
		}
	}
	static private enum LockType {
		Write,
		Read
	}
	
	static private class AModelLockInfo {
		/**
		 * model.
		 */
		public ModelInfo m;
		
		/**
		 * current lock type.<br>
		 * read lock and write lock are mutual exclusive.<br>
		 * write lock are exclusive each other.<br> 
		 */
		public LockType lock;
		
		/**
		 * owner thread.
		 */
		public Thread th;
		
		public AModelLockInfo(Thread th, ModelInfo m, LockType lock) {
			this.m = m;
			this.lock = lock;
			this.th = th;
		}
		
		@Override
		public String toString() {
			return "<lockInfo:"+lock+","+th+","+m+">";
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
	public AModelLockInfo readAccess(ModelInfo m, final long t) {
		long t2 = t;
		
		while (t<=0 || (t>0 && t2>0)) {
			synchronized (this) {
				if (noWriteLock(m) || isOwner(m)) {
					AModelLockInfo handle = createLock(m, LockType.Read);
					log.info("get:"+handle);
					return handle;
				} else {
					try {
						t2 -= read_wait_interval;
						this.wait(read_wait_interval);
					} catch (InterruptedException e) {}
				}
			}
		}
		
		return null;
	}
	
	public AModelLockInfo writeAccess(ModelInfo m, final long t) {
		long t2 = t;
		
		while (t<=0 || (t>0 && t2>0)) {
			synchronized (this) {	
				if (noLock(m) || onlyMine(m)) {
					AModelLockInfo handle = createLock(m, LockType.Write);
					log.info("get:"+handle);
					return handle;
				} else {
					try {
						t2 -= write_wait_interval;
						this.wait(write_wait_interval);
						System.out.println(Thread.currentThread()+".");
					} catch (InterruptedException e) {}
				}
			}
		}
		
		return null;
	}
	
	public synchronized void release(Object handle) {
		if (handle == null) return;
		
		ModelInfo m = ((AModelLockInfo)handle).m;
		List<AModelLockInfo> infos = this.model2MAList.get(m);
		if (infos != null) {
			for (int i=0; i<infos.size(); ++i) {
				AModelLockInfo info = infos.get(i);
				if (handle == info) {
					log.info("rel:"+handle);
					infos.remove(i);
					break;
				}
			}
			
			if (infos.size() == 0) {
				this.model2MAList.remove(m);
			}
		}
	}
	
	private boolean isOwner(ModelInfo m) {
		Thread th = Thread.currentThread();
		
		List<AModelLockInfo> infos = this.model2MAList.get(m);
		if (infos != null) {
			for (int i=0; i<infos.size(); ++i) {
				AModelLockInfo info = infos.get(i);
				if (info.th == th) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean onlyMine(ModelInfo m) {
		Thread th = Thread.currentThread();
		
		List<AModelLockInfo> infos = this.model2MAList.get(m);
		Thread owner = infos.get(0).th;
		return infos.size() == 1 && owner == th;
	}

	private boolean noLock(ModelInfo m) {
		return this.model2MAList.get(m) == null;
	}
	
	private boolean noWriteLock(ModelInfo m) {		
		List<AModelLockInfo> infos = this.model2MAList.get(m);
		if (infos != null) {
			for (int i=0; i<infos.size(); ++i) {
				AModelLockInfo info = infos.get(i);
				if (info.lock == LockType.Write) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * put a read lock.
	 * @param m
	 */
	private AModelLockInfo createLock(ModelInfo m, LockType lck) {
		List<AModelLockInfo> infos = this.model2MAList.get(m);
		if (infos == null) {
			infos = new ArrayList<AModelLockInfo>();
			this.model2MAList.put(m, infos);
		}
		
		Thread th = Thread.currentThread();
		AModelLockInfo info = new AModelLockInfo(th, m, lck);
		infos.add(info);
		
		return info;
	}
	
	/**
	 * Map a model to all the lock information.<br>
	 * If a model has a entry in this data, then the model must have been <br>
	 * locked by a thread.<br>
	 */
	private Map<ModelInfo,List<AModelLockInfo>> model2MAList = new HashMap<ModelInfo,List<AModelLockInfo>>();

	private Logger log = BTiaoLog.get();
}
