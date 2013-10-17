package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.CommonMgr;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.BlockInfo;
import com.btiao.product.domain.Position;

public class ResBTiaoBlockInfoGetFunc extends ResBTBase {
	@Override
	protected void pre() {
		String posId = this.getAttribute("positionId");
		urlIds.add(posId);
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		String func = form.getFirstValue("func");
		System.out.println(func);
		
		if (func.equals("normal")) {
			String lastId = form.getFirstValue("lastId");
			
			int num = 0;
			String numStr = form.getFirstValue("num");
			try {
				num = Integer.parseInt(numStr);
			} catch (Exception e) {
				String errMsg = "get func's argument error, num="+numStr;
				throw new BTiaoExp(ErrCode.WRONG_PARAM, e, errMsg);
			}
			
			if (lastId == null) {
				Position parentObj = new Position();
				parentObj.initId(urlIds);
				InfoMObject firstObj = CommonMgr.instance().getFirstRelObj(parentObj, RelName.blockInfo, BlockInfo.class);
				if (firstObj == null) {
					return new ArrayList<InfoMObject>();
				} else {
					List<InfoMObject> ret = new ArrayList<InfoMObject>();
					ret.add(firstObj);
					if (num > 1) {
						List<InfoMObject> otherRet = CommonMgr.instance().normalGet(firstObj, RelName.infoTimeSeq, num-1);
						ret.addAll(otherRet);
					}
					return ret;
				}
			} else {
				BlockInfo obj = new BlockInfo();
				List<String> urlIdsOfInfo = new ArrayList<String>(urlIds);
				urlIdsOfInfo.add(lastId);
				obj.initId(urlIdsOfInfo);
				return CommonMgr.instance().normalGet(obj, RelName.infoTimeSeq, num);
			}
		}
		
		return null;
	}

	@Override
	protected Object put(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
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

	private List<String> urlIds = new ArrayList<String>();
}
