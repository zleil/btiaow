package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.List;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.model.BTiaoRoot;
import com.btiao.base.model.BTiaoUser;
import com.btiao.base.oif.restlet.RestFilterBasicAuth;
import com.btiao.base.utils.BTiaoLog;
import com.btiao.common.service.ProductService;
import com.btiao.infomodel.InfoMObject;
import com.btiao.product.domain.BlockInfo;
import com.btiao.product.domain.Order;
import com.btiao.product.domain.Position;
import com.btiao.product.domain.UsrInfoExt;
import com.btiao.product.restlet.RestBTiaoGetAll.Def;


public class App extends Application {
	public Restlet createInboundRoot() {
		init();
		
		router.attach("/positions/{positionId}", ResBTiaoPosition.class);
		router.attach("/positions/{positionId}/infos/{infoId}", ResBTiaoBlockInfo.class);
		router.attach("/positions/{positionId}/orders/{orderId}", ResBTiaoOrder.class);
		router.attach("/getId/{idName}", ResBTiaoGetId.class);
		router.attach("/usrInfoExt/{usrId}", ResBTiaoUsrInfoExt.class);
		
		//router.attach("/positions", ResBTiaoPositionGetAll.class);
		//router.attach("/positions/{positionId}/infos", ResBTiaoBlockInfoGetFunc.class);
		//router.attach("/positions/{positionId}/orders", RestBTiaoGetAll.class);
		
		def("/positions",BTiaoRoot.class,Position.class,RelName.pos_of_root,RelName.timeSeq,ids(),ids("lastId"));
		def("/usrInfoExt",BTiaoRoot.class,UsrInfoExt.class,RelName.usrExtInfo_of_root,RelName.timeSeq,ids(),ids("lastId"));
		
		def("/positions/{posId}/orders",Position.class,Order.class,RelName.order_of_position,RelName.timeSeq,ids("posId"),ids("lastId"));
		def("/positions/{posId}/historyOrders",Position.class,Order.class,RelName.historyOrder_of_position,RelName.timeSeqHistory,ids("posId"),ids("lastId"));
		
		def("/users/{userId}/usrOrders",BTiaoUser.class,Order.class,RelName.order_of_user,RelName.ofUser_order,ids("userId"),ids("lastId"));
		def("/users/{userId}/usrHistoryOrders",BTiaoUser.class,Order.class,RelName.historyOrder_of_user,RelName.historyOfUser_order,ids("userId"),ids("lastId"));
		
		def("/positions/{posId}/infos",Position.class,BlockInfo.class,RelName.blockInfo_of_position,RelName.timeSeq,ids("posId"),ids("lastId"));
		def("/positions/{posId}/historyInfos",Position.class,BlockInfo.class,RelName.historyBlockInfo_of_position,RelName.timeSeqHistory,ids("posId"),ids("lastId"));
		
		def("/positions/{posId}/subPositions",Position.class,Position.class,RelName.subPos_of_pos,RelName.neighborPos,ids("posId"),ids("lastId"));
		
		RestFilterBasicAuth authFilter = new RestFilterBasicAuth();
		authFilter.setNext(router);
		
		BTiaoLog.get().warn("btiao/product restlet app is ok!");
		return authFilter;
	}
	
	private List<String> ids(String ... args) {
		List<String> r = new ArrayList<String>();
		for (String arg : args) {
			r.add(arg);
		}
		return r;
	}
	private void def(String url, 
			Class<?extends InfoMObject> parentClass,
			Class<?extends InfoMObject> objClass,
			String rightRelName, String downRelName,
			List<String> pidStrs, List<String> idStrs) {
		router.attach(url, RestBTiaoGetAll.class);
		RestBTiaoGetAll.def(new Def(url,parentClass,objClass,rightRelName,downRelName,pidStrs,idStrs));
	}
	
	private void init() {
		ProductService svc = ProductService.newService();
		svc.opUserId = "_mgr0";
		svc.base.begin();
		try {
			Position pos = new Position();
			
			pos.id = svc.getId(ProductService.IdMutex.posId.toString()).nextValue;
			pos.name = "中国";
			pos.ownerUser = "_mgr0";
			addPos(svc, pos);
			
			pos.pid = pos.id;
			pos.id = svc.getId(ProductService.IdMutex.posId.toString()).nextValue;
			pos.name = "北京";
			addPos(svc, pos);
			
			pos.pid = pos.id;
			pos.id = svc.getId(ProductService.IdMutex.posId.toString()).nextValue;
			pos.name = "朝阳区";
			addPos(svc, pos);
			
			pos.pid = pos.id;
			pos.id = svc.getId(ProductService.IdMutex.posId.toString()).nextValue;
			pos.name = "北苑家园";
			addPos(svc, pos);
			
			pos.pid = pos.id;
			pos.id = svc.getId(ProductService.IdMutex.posId.toString()).nextValue;
			pos.name = "望春园";
			addPos(svc, pos);
			
			pos.pid = pos.id;
			pos.id = svc.getId(ProductService.IdMutex.posId.toString()).nextValue;
			pos.name = "06000105小卖部";
			addPos(svc, pos);
			
			svc.base.success();
		} catch (Throwable e) {
			svc.base.failed();
			
			BTiaoLog.logExp(BTiaoLog.get(), e, "failed to init product app");
		} finally {
			svc.base.finish();
		}
	}
	
	private void addPos(ProductService svc, Position pos) throws BTiaoExp {
		svc.addObjectRightAndDownRel(RelName.pos_of_root, new BTiaoRoot(), pos, RelName.timeSeq, false);
		if (!pos.pid.equals("")) {
			Position parent = new Position();
			parent.id = pos.pid;
			svc.addObjectRightAndDownRel(RelName.subPos_of_pos, parent, pos, RelName.neighborPos, true);
		}
	}
	
	private Router router = new Router(getContext());
}

