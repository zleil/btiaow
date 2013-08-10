package com.btiao.infomodel;

import java.util.Collection;

import com.btiao.base.exp.BTiaoExp;

public abstract class InfoMService {
	static public synchronized InfoMService instance() {
		if (inst == null) {
			inst = new InfoMServiceImplNeo4j();
		}
		return inst;
	}

	static private InfoMService inst = null;
	
	/**
	 * get all the object that have a 'r' relation ship with o1.<br>
	 * @param o1 only the key attribute must be setted.
	 * @param r
	 * @return if there is no specified object, <br>
	 *         then it will return a collection with no element.<br>
	 */
	public abstract Collection<?> getRelObject(InfoMObject o1, RelType r, Class<?> o2Type) throws BTiaoExp;
	
	/**
	 * if there is a relation ship 'r' from a to b, then delete 'r' and b.<br>
	 * otherwise, do nothing.
	 * @param a only the key attribute must be setted.
	 * @param b only the key attribute must be setted.
	 * @param r
	 */
	public abstract void delBFromeA(InfoMObject a, InfoMObject b, RelType r) throws BTiaoExp;
	
	public abstract void begin();
	
	public abstract void end();
	
	protected InfoMService(){}
}
