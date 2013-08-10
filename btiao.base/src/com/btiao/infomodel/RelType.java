package com.btiao.infomodel;

public class RelType {
	public final String name;
	public RelType(String n) {
		name = n;
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
}
