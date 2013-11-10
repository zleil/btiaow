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
import com.btiao.product.domain.BlockInfo;
import com.btiao.product.domain.Position;

public class ResBTiaoBlockInfo extends ResBTBase {
	@Override
	protected void pre() {
		String posId = this.getAttribute("positionId");
		String infoId = this.getAttribute("infoId");
		urlIds.add(posId);
		urlIds.add(infoId);
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		return CommonMgr.instance().getInfoObject(BlockInfo.class, urlIds);
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.BlockInfo")
	protected Object put(Object arg) throws BTiaoExp {
		String lastUrlId = urlIds.get(urlIds.size()-1);
		if (!lastUrlId.endsWith(OBJID_WHEN_CREATE)){
			String errMsg = "last id in url is not equals to " + OBJID_WHEN_CREATE;
			throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
		}

		Position pos = new Position();
		pos.initId(new ArrayList<String>(urlIds.subList(0, urlIds.size()-1)));
		CommonMgr.instance().addTimeSeqInfoObject(RelName.blockInfo, pos, (InfoMObject)arg);
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.BlockInfo")
	protected Object post(Object arg, Collection<String> attrList)
			throws BTiaoExp {
		InfoMObject info = (InfoMObject)arg;
		info.initId(urlIds);
				
		CommonMgr.instance().updateObject(info, attrList);
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		BlockInfo info = new BlockInfo();
		info.initId(urlIds);
		
		Position pos = new Position();
		pos.initId(new ArrayList<String>(urlIds.subList(0, urlIds.size()-1)));
		CommonMgr.instance().delTimeSeqInfoObject(RelName.blockInfo, pos, (InfoMObject)info);
		return null;
	}
	
	protected List<String> urlIds = new ArrayList<String>();
}
