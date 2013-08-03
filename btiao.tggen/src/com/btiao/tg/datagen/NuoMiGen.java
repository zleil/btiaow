package com.btiao.tg.datagen;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Node;

import com.btiao.tg.TgData;
import com.btiao.tg.TgShop;

public class NuoMiGen extends WoWoGen {

	@Override
	protected boolean genTg() {
		do { try {
			
		if (index >= tgNodes.size()) {
			return false;
		}
		
		Node tgNode = tgNodes.get(index++);
		
		tgTmp = new TgData();
		tgTmp.url = ((Node)tgNode.selectNodes("loc").get(0)).getText();
		tgTmp.title = ((Node)tgNode.selectNodes("data/display/title").get(0)).getText();
		tgTmp.imageUrl = ((Node)tgNode.selectNodes("data/display/image").get(0)).getText();
		tgTmp.startTime = tmStr2Long(((Node)tgNode.selectNodes("data/display/startTime").get(0)).getText());
		tgTmp.endTime = tmStr2Long(((Node)tgNode.selectNodes("data/display/endTime").get(0)).getText());
		String type = ((Node)tgNode.selectNodes("data/display/secondCategory").get(0)).getText();
		List<String> typeList = new ArrayList<String>();
		typeList.add(type);
		tgTmp.type = convertType(typeList);
		tgTmp.value = priceStr2Int(((Node)tgNode.selectNodes("data/display/value").get(0)).getText());
		tgTmp.price = priceStr2Int(((Node)tgNode.selectNodes("data/display/price").get(0)).getText());
		tgTmp.boughtNum = str2Int(((Node)tgNode.selectNodes("data/display/bought").get(0)).getText());
		tgTmp.desc = ((Node)tgNode.selectNodes("data/display/title").get(0)).getText();
		tgTmp.useEndTime = tgTmp.endTime; //tmStr2Long(((Node)tgNode.selectNodes("data/display/merchantEndTime").get(0)).getText());
		
		shopsTmp.clear();
		
		@SuppressWarnings("unchecked")
		List<Node> shopNodes = (List<Node>)tgNode.selectNodes("data/shops/shop");
		for (Node shopNode : shopNodes) {
			TgShop shop = new TgShop();
			shopsTmp.add(shop);
			
			shop.name = ((Node)shopNode.selectNodes("name").get(0)).getText();
			shop.tel = ((Node)shopNode.selectNodes("tel").get(0)).getText();
			shop.addr = ((Node)shopNode.selectNodes("addr").get(0)).getText();
			shop.shopArea = ((Node)shopNode.selectNodes("area").get(0)).getText();
			shop.longitude = doubleLatLon2Long(((Node)shopNode.selectNodes("longitude").get(0)).getText());
			shop.latitude = doubleLatLon2Long(((Node)shopNode.selectNodes("latitude").get(0)).getText());
			
			//高德地图坐标与百度地图坐标有些误差，大概就偏下
			shop.longitude += 6400;
			shop.latitude += 5730;
		}
		
		return true;
		
		} catch (Exception e) {
			e.printStackTrace();
			
			continue;
		} finally {
			;
		} } while (true);
	}

	@Override
	protected String getName() {
		return "lashou";
	}

	@Override
	protected void preGen() {
		tgNodes = (List<Node>)doc.selectNodes("/urlset/url");
		unkownTypeStrs.clear();
	}

	@Override
	protected void postGen() throws Exception {
		super.postGen();
	}

}
