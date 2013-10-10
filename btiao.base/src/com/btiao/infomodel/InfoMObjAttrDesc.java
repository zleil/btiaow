package com.btiao.infomodel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface InfoMObjAttrDesc {
	/**
	 * if the attribute of object is a key, then you should set it.<br>
	 * otherwise you have no need to care it.<br>
	 * @return
	 */
	boolean key() default false;
	
	/**
	 * if the attribute of object should not be store in info model,<br>
	 * then, it should be set false. otherwise you have no need to care it.<br>
	 * @return
	 */
	boolean store() default true;
}
