package com.btiao.product.domain;

import com.btiao.infomodel.InfoMObject;

public class Product extends InfoMObject {
	public long id;
	public String desc;
	public int price;
	public int oldPrice;
	public String imageUrl;
	
	public int evaluteValue;
}
