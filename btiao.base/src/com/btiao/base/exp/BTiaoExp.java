package com.btiao.base.exp;

public class BTiaoExp extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5694149100044209600L;

	public BTiaoExp(int errNo, String extInfo) {
		super(extInfo);
		this.errNo = errNo;
		this.src = null;
	}
	public BTiaoExp(int errNo, Throwable src) {
		this.errNo = errNo;
		this.src = src;
	}
	
	public BTiaoExp(int errNo, Throwable src, String extInfo) {
		super(extInfo);
		
		this.errNo = errNo;
		this.src = src;
	}
	
	@Override
	public String toString() {
		return super.toString()+",errNo=" + errNo + "\nsrcExp=" + src;
	}
	
	public final int errNo;
	
	public final Throwable src;
}
