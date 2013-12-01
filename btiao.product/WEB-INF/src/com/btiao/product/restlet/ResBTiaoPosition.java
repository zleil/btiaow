package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.restlet.data.Form;
import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.model.BTiaoRoot;
import com.btiao.base.model.BTiaoUser;
import com.btiao.base.model.FriendUserId;
import com.btiao.base.oif.restlet.JsonCvtInfo;
import com.btiao.base.utils.BTiaoLog;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.Position;

public class ResBTiaoPosition extends ResBTiaoProduct {
	@Override
	protected void pre() {
		super.pre();
		
		String posId = this.getAttribute("positionId");
		urlIds.add(posId);
	}
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		return svc.getObject(Position.class, urlIds);
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.Position")
	protected Object put(Object arg) throws BTiaoExp {
		Position pos = (Position)arg;
//		String lastUrlId = urlIds.get(urlIds.size()-1);
//		if (!lastUrlId.endsWith(OBJID_WHEN_CREATE)){
//			String errMsg = "last id in url is not equals to " + OBJID_WHEN_CREATE;
//			throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
//		}

		svc.base.begin();
		try {
			svc.addObjectRightAndDownRel(RelName.pos_of_root, new BTiaoRoot(), pos, RelName.timeSeq, false);
			Position parent = getParentPos(pos);
			if (parent != null) {
				svc.addObjectRightAndDownRel(RelName.subPos_of_pos, parent, pos, RelName.neighborPos, true);
			}
			svc.base.success();
		} catch (Throwable e) {
			svc.base.failed();
			BTiaoLog.logExp(BTiaoLog.get(), e, "put position failed!position="+pos);
			
			throw e;
		} finally {
			svc.base.finish();
		}
		return null;
	}
	
	private Position getParentPos(Position pos) {
		if (pos.pid.equals("") || pos.pid == null) {
			return null;
		}
		Position parent = new Position();
		parent.id = pos.pid;
		return parent;
	}

	@Override
	@JsonCvtInfo(objClassName="com.btiao.product.domain.Position")
	protected Object post(Object arg, Collection<String> attrs) throws BTiaoExp {
		Position pos = (Position)arg;
		pos.initId(urlIds);
		
		svc.base.begin();
		try {
			if (!pos.pid.equals(new Position().pid)) {
				String errMsg = "can't modify pid of position!";
				throw new BTiaoExp(ErrCode.WRONG_PARAM, null, errMsg);
			}
			
			if (attrs.contains(Position.FUID_NAME)) {
				FriendUserId u = new FriendUserId();
				u.friendId = pos.fuid;
				if (!svc.base.get(u)) {
					throw new BTiaoExp(ErrCode.CNG_POS_OWNER_OWNER_NOT_EXIST, "target="+pos+",newOwner="+pos.ownerUser);
				}
				pos.ownerUser = u.deviceId;
			}
			
			svc.updateObject(pos, attrs);
			svc.base.success();
		} catch (BTiaoExp e) {
			svc.base.failed();
			throw e;
		} finally {
			svc.base.finish();
		}
		return null;
	}

	@Override
	protected Object del(Object arg) throws BTiaoExp {
		Position pos = new Position();
		pos.initId(urlIds);
		
		svc.base.begin();
		try {
			if (hasSubPos(pos)) {
				throw new BTiaoExp(ErrCode.CANNOT_DEL_POS_HAVE_SUBS, "pos="+pos);
			}
			Position parent = getParentPos(pos);
			if (parent != null) {
				svc.delObjectRightAndDownRel(RelName.subPos_of_pos, parent, pos, RelName.neighborPos, true);
			}
			svc.delObjectRightAndDownRel(RelName.pos_of_root, new BTiaoRoot(), pos, RelName.timeSeq, false);
			
			svc.base.success();
		} catch (Throwable e) {
			svc.base.failed();
			BTiaoLog.logExp(BTiaoLog.get(), e, "del position failed!position="+pos);
			
			throw e;
		} finally {
			svc.base.finish();
		}
		
		return null;
	}
	
	private boolean hasSubPos(Position pos) throws BTiaoExp {
		List<InfoMObject> subs;
		try {
			subs = svc.getAllObjRightAndDownRel(pos, 
					Position.class, RelName.subPos_of_pos,
					null, RelName.neighborPos, 1);
			return subs.size() > 0;
		} catch (BTiaoExp e) {
			throw e;
		}
		
	}

	protected List<String> urlIds = new ArrayList<String>();
}
