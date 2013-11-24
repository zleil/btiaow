package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.restlet.data.Form;
import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.model.BTiaoRoot;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.FriendUserId;
import com.btiao.product.domain.UsrInfoExt;

public class ResBTiaoUsrInfoExt extends ResBTiaoProduct {
	@Override
	protected void pre() {
		super.pre();
		
		usrId = this.getAttribute("usrId");
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		ArrayList<String> idList = new ArrayList<String>();
		idList.add(usrId);
		Object r = svc.getObject(UsrInfoExt.class, idList);
		return r;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.UsrInfoExt")
	protected Object put(Object arg) throws BTiaoExp {
		UsrInfoExt ext = (UsrInfoExt) arg;
		ext.usrId = this.opUserId;//用户信息只能由用户自己创建
		ext.ownerUser = this.opUserId;//Owner是自己
		
		svc.base.begin();
		try {
			if (ext.friendUid.equals("")) {
				throw new BTiaoExp(ErrCode.WRONG_FRIEND_UID);
			}
			
			FriendUserId fuid = new FriendUserId();
			fuid.friendId = ext.friendUid;
			fuid.deviceId = ext.usrId;
			
			svc.base.add(fuid);
			svc.addObjectRightAndDownRel(RelName.usrExtInfo_of_root, new BTiaoRoot(), ext, RelName.timeSeq, false);
			
			svc.base.success();
		}catch (BTiaoExp e) {
			svc.base.failed();
			throw e;
		} finally {
			svc.base.finish();
		}
		return null;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.UsrInfoExt")
	protected Object post(Object arg, Collection<String> attrList)
			throws BTiaoExp {
		UsrInfoExt ext = (UsrInfoExt) arg;
		ext.usrId = this.opUserId;//用户信息只能由用户自己创建
		ext.ownerUser = this.opUserId;//Owner是自己
		
		try {
			List<String> attrList2 = new ArrayList<String>();
			for (String attr : attrList) {
				if (attr.equals(UsrInfoExt.FRIEND_UID_ATTR) ||
					attr.equals(InfoMObject.OWNER_USER_ATTR)) {
					continue;
				}
				
				attrList2.add(attr);
			}
			
			svc.updateObject(ext, attrList);
		} catch (BTiaoExp e) {
			throw e;
		}
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		// TODO Auto-generated method stub
		return null;
	}

	private String usrId = "";
}
