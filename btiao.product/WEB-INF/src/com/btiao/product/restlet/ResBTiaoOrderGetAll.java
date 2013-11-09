package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.restlet.data.Form;
import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.CommonMgr;
import com.btiao.product.domain.Order;
import com.btiao.product.domain.Position;

public class ResBTiaoOrderGetAll extends ResBTBase {
	@Override
	protected void pre() {
		String posId = this.getAttribute("positionId");
		urlIds.add(posId);
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		String func = form.getFirstValue("func");
		
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
			
			Position parentObj = new Position();
			parentObj.initId(urlIds);
			
			Order lastObj = new Order();
			List<String> urlIdsOfInfo = new ArrayList<String>(urlIds);
			if (lastId == null || lastId.equals("")) {
				lastObj = null;
			} else {
				urlIdsOfInfo.add(lastId);
				lastObj.initId(urlIdsOfInfo);
			}
			
			return mgr.getAllRightAndDownObj(parentObj, 
					Order.class, RelName.order,
					lastObj, RelName.timeSeq, num);
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
	
	private CommonMgr mgr = CommonMgr.instance();
}
