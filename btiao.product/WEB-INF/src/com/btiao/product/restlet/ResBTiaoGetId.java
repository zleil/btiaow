package com.btiao.product.restlet;

import java.util.Collection;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.infomodel.InfoMBaseService;
import com.btiao.product.domain.BTiaoId;

public class ResBTiaoGetId extends ResBTBase {
	@Override
	protected void pre() {
		idName = this.getAttribute("idName");
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object put(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.BTiaoId")
	protected Object post(Object arg, Collection<String> attrList)
			throws BTiaoExp {
		synchronized (this.getClass()) {
			BTiaoId idObj = new BTiaoId();
			idObj.idName = idName;
			
			base.begin();
			try {
				if (!base.get(idObj)) {
					base.add(idObj);
				} else {
					idObj.nextValue();
					base.mdf(idObj);
				}
				base.success();
			} catch (BTiaoExp e) {
				base.failed();
			} finally {
				base.finish();
			}
			
			return idObj;
		}
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	private String idName;
	public InfoMBaseService base = InfoMBaseService.instance();
}
