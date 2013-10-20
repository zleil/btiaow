package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.CommonMgr;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.Position;

public class ResBTiaoPosition extends ResBTBase {
	@Override
	protected void pre() {
		String posId = this.getAttribute("positionId");
		urlIds.add(posId);
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		return CommonMgr.instance().getInfoObject(Position.class, urlIds);
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.Position")
	protected Object put(Object arg) throws BTiaoExp {
		InfoMObject info = (InfoMObject)arg;
		String lastUrlId = urlIds.get(urlIds.size()-1);
		if (!lastUrlId.endsWith(OBJID_WHEN_CREATE)){
			String errMsg = "last id in url is not equals to " + OBJID_WHEN_CREATE;
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
		}

		CommonMgr.instance().addInfoObject(info);
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.Position")
	protected Object post(Object arg, Collection<String> attrs) throws BTiaoExp {
		InfoMObject info = (InfoMObject)arg;
		info.initId(urlIds);
		
		if (!((Position)info).pid.equals(new Position().pid)) {
			String errMsg = "can't modify pid of position!";
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
		}
		
		CommonMgr.instance().updateInfoObject(info, attrs);
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		Position info = new Position();
		info.initId(urlIds);
		CommonMgr.instance().delInfoObject(info);
		return null;
	}

	protected List<String> urlIds = new ArrayList<String>();
}
