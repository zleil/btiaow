package com.btiao.tg.datagen;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Node;

import com.btiao.tg.TgData;
import com.btiao.tg.TgShop;

public class MeiTuanGen extends WoWoGen {

	@Override
	protected boolean genTg() {
		do { try {
			
		if (index >= tgNodes.size()) {
			return false;
		}
		
		Node tgNode = (Node)(tgNodes.get(index).selectNodes("deal").get(0));
		
		tgTmp = new TgData();
		tgTmp.url = ((Node)tgNode.selectNodes("deal_url").get(0)).getText();
		tgTmp.title = ((Node)tgNode.selectNodes("deal_title").get(0)).getText();
		tgTmp.imageUrl = ((Node)tgNode.selectNodes("deal_img").get(0)).getText();
		tgTmp.startTime = Long.parseLong(((Node)tgNode.selectNodes("start_time").get(0)).getText());
		tgTmp.endTime = Long.parseLong(((Node)tgNode.selectNodes("end_time").get(0)).getText());
		String type = ((Node)tgNode.selectNodes("deal_subcate").get(0)).getText();
		List<String> typeList = new ArrayList<String>();
		typeList.add(type);
		tgTmp.type = convertType(typeList);
		tgTmp.value = priceStr2Int(((Node)tgNode.selectNodes("value").get(0)).getText());
		tgTmp.price = priceStr2Int(((Node)tgNode.selectNodes("price").get(0)).getText());
		String boughtNumStr = ((Node)tgNode.selectNodes("sold_out").get(0)).getText();
		if (boughtNumStr.equals("no")) {
			tgTmp.boughtNum = 0;
		} else {
			tgTmp.boughtNum = str2Int(boughtNumStr);
		}
		tgTmp.desc = ((Node)tgNode.selectNodes("deal_desc").get(0)).getText();
		tgTmp.useEndTime = tmStr2Long(((Node)tgNode.selectNodes("coupon_end_time").get(0)).getText());
		
		shopsTmp.clear();
		
		@SuppressWarnings("unchecked")
		List<Node> shopNodes = tgNodes.get(index).selectNodes("shops/shop");
		for (Node shopNode : shopNodes) {
			TgShop shop = new TgShop();
			shopsTmp.add(shop);
			
			shop.name = ((Node)shopNode.selectNodes("shop_name").get(0)).getText();
			tgTmp.desc = "【" + shop.name + "】" + tgTmp.desc;
			shop.tel = ((Node)shopNode.selectNodes("shop_tel").get(0)).getText();
			shop.addr = ((Node)shopNode.selectNodes("shop_addr").get(0)).getText();
			shop.shopArea = ((Node)shopNode.selectNodes("shop_area").get(0)).getText();
			shop.longitude = doubleLatLon2Long(((Node)shopNode.selectNodes("shop_long").get(0)).getText());
			shop.latitude = doubleLatLon2Long(((Node)shopNode.selectNodes("shop_lat").get(0)).getText());
			
			//sogo地图与百度地图的偏差，做微调
			shop.longitude += 6400;
			shop.latitude += 5730;
		}
		
		return true;
		
		} catch (Exception e) {
			e.printStackTrace();
			
			continue;
		} finally {
			++index;
		} } while (true);
	}

	@Override
	protected String getName() {
		return "meituan";
	}

	@Override
	protected void preGen() {
		tgNodes = (List<Node>)doc.selectNodes("/response/deals/data");
		unkownTypeStrs.clear();
	}

	@Override
	protected void postGen() throws Exception {
		super.postGen();
	}

}
