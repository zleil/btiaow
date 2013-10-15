package com.btiao.product.restlet;

import java.util.Collection;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.CommonMgr;
import com.btiao.product.domain.Position;

public class ResBTiaoPosition extends ResBTBase {
	@Override
	protected void pre() {
		posId = this.getAttribute("positionId");
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		return CommonMgr.instance().getInfoObject(Position.class, posId);
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.Position")
	protected Object put(Object arg) throws BTiaoExp {
		Position info = (Position)arg;
		if (!posId.endsWith(OBJID_WHEN_CREATE)){
			String errMsg = "id in url is not equals to " + OBJID_WHEN_CREATE;
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
		}

		CommonMgr.instance().addInfoObject(info);
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.Position")
	protected Object post(Object arg, Collection<String> attrs) throws BTiaoExp {
		Position info = (Position)arg;
		if (!info.id.equals(posId)){
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null);
		}
		
		CommonMgr.instance().updateInfoObject(info, attrs);
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		CommonMgr.instance().delInfoObject(Position.class, posId);
		return null;
	}

	private String posId;
}
