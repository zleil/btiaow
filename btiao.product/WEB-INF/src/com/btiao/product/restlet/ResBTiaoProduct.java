package com.btiao.product.restlet;

import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.ProductService;

public abstract class ResBTiaoProduct extends ResBTBase {

	@Override
	protected void pre() {
		super.pre();
		
		svc.opUserId = this.opUserId;
	}

	protected ProductService svc = ProductService.newService();
}
