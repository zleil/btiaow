package com.btiao.infomodel;

import java.lang.reflect.Field;
import java.util.Collection;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;

public class InfoMObject implements Cloneable {
	/**
	 * update some attributes from newObj
	 * @param newObj
	 * @param attrs only the specified attributes will be modified.
	 * @throws BTiaoExp
	 */
	public void update(InfoMObject newObj, Collection<String> attrs) throws BTiaoExp{
		for (String attrName : attrs) {
			try {
				Field f = newObj.getClass().getField(attrName);
				Object v = f.get(newObj);
				f.set(this, v);
			} catch (Exception e) {
				throw new BTiaoExp(ErrCode.INTERNEL_ERROR, e);
			}
		}
	}
	
	@Override
	public InfoMObject clone() {
		try {
			return (InfoMObject)(super.clone());
		} catch (Exception e) {
			return null;
		}
	}
}
