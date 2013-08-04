package com.btiao.base.oif.restlet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonCvtInfo {
	String objClassName();
}
