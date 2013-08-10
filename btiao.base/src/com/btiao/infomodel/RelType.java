package com.btiao.infomodel;

public class RelType {
	public RelType(String n) {
		name = n;
	}
	
	public String name() {
		return name;
	}
	
	public boolean equals(Object o) {
		if (o.getClass() != this.getClass()) return false;
		
		RelType oo = (RelType)o;
		return name.equals(oo.name);
	}
	
	public int hasCode() {
		return name.hashCode();
	}
	
	public String toString() {
		return "rel<"+name+">";
	}
	
	/**
	 * relation ship name.<br>
	 * in the info model, two object can only has at most one ship with one name.<br>
	 */
	public final String name;
}
