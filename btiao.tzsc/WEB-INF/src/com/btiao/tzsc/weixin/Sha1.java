package com.btiao.tzsc.weixin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;

public class Sha1 {

	static public void main(String[] args) {
		System.out.println(Sha1.doit("cqzytsyrjnnjdwiloveurebecca"));
		
		HashSet<String> set = new HashSet<String>();
		set.add("a");
		set.add("c");
		set.add("b");
		Iterator<String> it = set.iterator();
		
		StringBuilder sb = new StringBuilder();
		sb.append(it.next());
		sb.append(it.next());
		sb.append(it.next());
		
		System.out.println(sb);
		
	}
	
	public static String doit(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");     //选择SHA-1，也可以选择MD5
            byte[] digest = md.digest(inStr.getBytes()); //返回的是byet[]，要转化为String存储比较方便
            outStr = bytetoString(digest);
        }
        catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }
	
	public static String bytetoString(byte[] digest) {
        String str = "";
        String tempStr = "";
        
        for (int i = 0; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1) {
                str = str + "0" + tempStr;
            }
            else {
                str = str + tempStr;
            }
        }
        return str.toLowerCase();
    }
}
