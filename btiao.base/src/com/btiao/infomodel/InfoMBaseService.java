package com.btiao.infomodel;

import com.btiao.base.exp.BTiaoExp;

public abstract class InfoMBaseService {
	static public synchronized InfoMBaseService instance() {
		if (inst == null) {
			inst = new InfoModelServiceImplNeo4j();
		}
		return inst;
	}

	static private InfoMBaseService inst = null;
	
	/**
	 * if there the object of u does not exist in info model, then add it.<br>
	 * otherwise, it will throw a exception.<br>
	 * @param u object infomation.
	 * @throws BTiaoExp
	 */
	public abstract void add(InfoMObject u) throws BTiaoExp;
	
	/**
	 * query the infomation of one object from info model.<br>
	 * if the object does not exist in info model, a exception will be throwed.<br>
	 * @param u the object key attribute must be setted in this argument.<br>
	 *          other attributes may not be setted.
	 * @throws BTiaoExp
	 */
	public abstract void get(InfoMObject u) throws BTiaoExp;
	
	/**
	 * delete a object exists in info model.<br>
	 * if there are some constrain which forbid this action, <br>
	 * then a exception will be throwed.<br>
	 * @param u only the key attribute of u must be setted.
	 * @throws BTiaoExp
	 */
	public abstract void del(InfoMObject u) throws BTiaoExp;
	
	/**
	 * modify the information of specified object in info model fully as object 'u'.<br>
	 * if the object does not exist in info model, a exception will be throwed.<br>
	 * @param u 
	 * @throws BTiaoExp
	 */
	public abstract void mdf(InfoMObject u) throws BTiaoExp;
	
	/**
	 * make a relation ship between object o1 and o2.<br>
	 * @param o1 only the key attribute must be setted.
	 * @param o2 only the key attribute must be setted.
	 * @param r
	 * @throws BTiaoExp
	 */
	public abstract void addRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp;

	/**
	 * check whether there is a relation ship between object o1 and o2.<br>
	 * @param o1 only the key attribute must be setted.
	 * @param o2 only the key attribute must be setted.
	 * @param r
	 * @return return true if there has the specified ship, otherwise return false
	 * @throws BTiaoExp
	 */
	public abstract boolean hasRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp;
	
	/**
	 * delete the relation ship between object o1 and o2.
	 * @param o1 only the key attribute must be setted.
	 * @param o2 only the key attribute must be setted.
	 * @param r
	 * @throws BTiaoExp
	 */
	public abstract void delRel(InfoMObject o1, InfoMObject o2, RelType r) throws BTiaoExp;
	
	public abstract void begin();
	
	public abstract void end();
	
	//public abstract void setRelProp(Object o1, Object o2, RelType r) throws BTiaoExp;
	
	//public abstract void delRelProp(Object o1, Object o2, RelType r) throws BTiaoExp;
	
	//public abstract void getRelProp(Object o1, Object o2, RelType r) throws BTiaoExp;
	
	protected InfoMBaseService(){}
}
