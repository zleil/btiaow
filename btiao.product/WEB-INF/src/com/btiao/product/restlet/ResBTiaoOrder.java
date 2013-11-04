package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.model.BTiaoUser;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.CommonMgr;
import com.btiao.infomodel.InfoMBaseService;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.Order;
import com.btiao.product.domain.Position;

public class ResBTiaoOrder extends ResBTBase {
	@Override
	protected void pre() {
		String posId = this.getAttribute("positionId");
		String orderId = this.getAttribute("orderId");
		urlIds.add(posId);
		urlIds.add(orderId);
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.Order")
	protected Object put(Object arg) throws BTiaoExp {
		String lastUrlId = urlIds.get(urlIds.size()-1);
		if (!lastUrlId.endsWith(OBJID_WHEN_CREATE)){
			String errMsg = "last id in url is not equals to " + OBJID_WHEN_CREATE;
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
		}

		Position pos = new Position();
		pos.initId(new ArrayList<String>(urlIds.subList(0, urlIds.size()-1)));
		
		BTiaoUser fromUser = new BTiaoUser();
		{
			ArrayList<String> array = new ArrayList<String>();
			array.add((((Order)arg).fromUser));
			fromUser.initId(array);
		}
		
		InfoMBaseService base = CommonMgr.instance().base;
		base.begin();
		try {
			CommonMgr.instance().addObjectRightAndDownRel(RelName.order, pos, (InfoMObject)arg, RelName.timeSeq);
			CommonMgr.instance().addObjectRelRightAndDownRel(RelName.order, fromUser, (InfoMObject)arg, RelName.userOrder);
			base.success();
		} catch (BTiaoExp e) {
			base.failed();
			throw e;
		} finally {
			base.finish();
		}
		
		return null;
	}

	@Override
	protected Object post(Object arg, Collection<String> attrList)
			throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	protected List<String> urlIds = new ArrayList<String>();
}
