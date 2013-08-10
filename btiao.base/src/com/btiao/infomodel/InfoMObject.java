package com.btiao.infomodel;

public class InfoMObject implements Cloneable {
	public InfoMObject clone() {
		try {
			return (InfoMObject)(super.clone());
		} catch (Exception e) {
			return null;
		}
	}
}
