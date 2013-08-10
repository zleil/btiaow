package com.btiao.infomodel;

import java.util.Collection;

public abstract class InfoMService {
	static public InfoMService instance() {
		if (inst == null) {
			inst = new InfoMServiceImplNeo4j();
		}
		return inst;
	}

	static private InfoMService inst = null;
	
	/**
	 * get all the object that has a 'r' relation ship with o1.<br>
	 * @param o1
	 * @param r
	 * @return if there is no specified object, <br>
	 *         then it will return a collection with no element.<br>
	 */
	public abstract Collection<?> getRelObject(Object o1, RelType r);
}
