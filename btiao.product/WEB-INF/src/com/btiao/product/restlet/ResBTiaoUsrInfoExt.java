package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;

import org.restlet.data.Form;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.model.BTiaoRoot;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.CommonMgr;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.UsrInfoExt;

public class ResBTiaoUsrInfoExt extends ResBTBase {
	@Override
	protected void pre() {
		usrId = this.getAttribute("usrId");
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		ArrayList<String> idList = new ArrayList<String>();
		idList.add(usrId);
		Object r = mgr.getObject(UsrInfoExt.class, idList);
		return r;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.UsrInfoExt")
	protected Object put(Object arg) throws BTiaoExp {
		mgr.addObjectRightAndDownRel(RelName.usrExtInfoOfRoot, new BTiaoRoot(), (InfoMObject) arg, RelName.timeSeq, false);
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.UsrInfoExt")
	protected Object post(Object arg, Collection<String> attrList)
			throws BTiaoExp {
		try {
			mgr.updateObject((InfoMObject)arg, attrList);
		} catch (BTiaoExp e) {
			if (e.errNo == ErrCode.OBJ_NOT_IN_INFO_MODEL) {
				mgr.addObjectRightAndDownRel(RelName.usrExtInfoOfRoot, new BTiaoRoot(), (InfoMObject) arg, RelName.timeSeq, false);
			}
		}
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	private String usrId = "";
	private CommonMgr mgr = CommonMgr.instance();
}
