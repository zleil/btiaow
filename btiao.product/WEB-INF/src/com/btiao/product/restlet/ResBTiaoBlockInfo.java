package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import org.restlet.data.Form;
import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.base.utils.SimpleFunc;
import com.btiao.common.service.ProductService;
import com.btiao.infomodel.InfoMBaseService;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.BlockInfo;
import com.btiao.product.domain.BlockInfo.State;
import com.btiao.product.domain.Position;

public class ResBTiaoBlockInfo extends ResBTBase {
	@Override
	protected void pre() {
		String posId = this.getAttribute("positionId");
		String infoId = this.getAttribute("infoId");
		
		infoIdList.add(infoId);
		posIdList.add(posId);
		
		svc.opUserId = this.opUserId;
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		return svc.getObject(BlockInfo.class, infoIdList);
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.BlockInfo")
	protected Object put(Object arg) throws BTiaoExp {
//		String lastUrlId = urlIds.get(urlIds.size()-1);
//		if (!lastUrlId.endsWith(OBJID_WHEN_CREATE)){
//			String errMsg = "last id in url is not equals to " + OBJID_WHEN_CREATE;
//			throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
//		}
		Position pos = new Position();
		pos.initId(posIdList);
		svc.addObjectRightAndDownRel(RelName.blockInfo_of_position, pos, (InfoMObject)arg, RelName.timeSeq, false);
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.BlockInfo")
	protected Object post(Object arg, Collection<String> attrList)
			throws BTiaoExp {
		Position pos = new Position();
		pos.initId(posIdList);
		
		BlockInfo info = (BlockInfo)arg;
		
		InfoMBaseService base = svc.base;
		base.begin();
		try {
			BlockInfo infoOld = (BlockInfo) svc.getObject(BlockInfo.class, SimpleFunc.getArrayList(info.id));
			if (infoOld.state != info.state && infoOld.state == State.VALID) {
				svc.delObjectRightAndDownRel(RelName.blockInfo_of_position, pos, info, RelName.timeSeq, true);
				svc.addObjectRightAndDownRel(RelName.historyBlockInfo_of_position, pos, info, RelName.timeSeqHistory, true);
			}
			svc.updateObject(info, attrList);
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
	protected Object del(Object arg) throws BTiaoExp {		
		BlockInfo info = new BlockInfo();
		info.initId(infoIdList);
		
		Position pos = new Position();
		pos.initId(posIdList);
		
		svc.delObjectRightAndDownRel(RelName.blockInfo_of_position, pos, info, RelName.timeSeq, false);
		return null;
	}
	
	private ProductService svc = ProductService.newService();
	private ArrayList<String> infoIdList = new ArrayList<String>();
	private ArrayList<String> posIdList = new ArrayList<String>();
}
