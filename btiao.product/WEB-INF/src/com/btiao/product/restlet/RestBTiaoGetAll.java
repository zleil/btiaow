package com.btiao.product.restlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.restlet.data.Form;
import org.restlet.data.Reference;

import com.btiao.base.exp.BTiaoExp;
import com.btiao.base.exp.ErrCode;
import com.btiao.base.oif.restlet.ResBTBase;
import com.btiao.common.service.CommonMgr;
import com.btiao.infomodel.InfoMObject;

public class RestBTiaoGetAll extends ResBTBase {
	static public class Def {
		public String idOfUrl; 
		public Class<?extends InfoMObject> parentClass;
		public Class<?extends InfoMObject> objClass;
		public String rightRelName;
		public String downRelName;
		public List<String> pidStrs;
		public List<String> idStrs;
		
		public Def(String url, 
			Class<?extends InfoMObject> parentClass,
			Class<?extends InfoMObject> objClass,
			String rightRelName, String downRelName,
			List<String> pidStrs, List<String> idStrs)
		{
			this.idOfUrl = getLastPartFromUrl(url);
			this.parentClass = parentClass;
			this.objClass = objClass;
			this.rightRelName = rightRelName;
			this.downRelName = downRelName;
			this.pidStrs = pidStrs;
			this.idStrs = idStrs;
		}
	}
	
	static public void def(Def arg) {
		defs.put(arg.idOfUrl, arg);
	}
	
	static private String getLastPartFromUrl(String url) {
		String[] split = url.split("/");
		String[] split2 = split[split.length-1].split("\\?");
		return split2[0];
	}
	
	static private Map<String,Def> defs = new HashMap<String,Def>();
	
	@Override
	protected Object get(Form form) throws BTiaoExp {
		String func = form.getFirstValue("func");
		
		Reference ref = this.getRequest().getOriginalRef();
		String url = ref.getIdentifier();
		String idOfGetAll = getLastPartFromUrl(url);
		def = defs.get(idOfGetAll);
		
		if (def == null) {
			throw new BTiaoExp(ErrCode.OBJ_NOT_IN_INFO_MODEL, "getall for a not-existing object url="+url);
		}
		
		if (func.equals("normal")) {
			getArg(form);
			InfoMObject parentObj = getParentObj();
			InfoMObject lastObj = getLastObj();
			
			return mgr.getAllRightAndDownObj(parentObj, 
					def.objClass, def.rightRelName,
					lastObj, def.downRelName, num);
		}
		return null;
	}
	
	protected InfoMObject getParentObj() throws BTiaoExp {
		InfoMObject parentObj = null;
		try {
			parentObj = def.parentClass.newInstance();
		} catch (Exception e) {
			throw new BTiaoExp(ErrCode.INTERNEL_ERROR, e, "failed to get parent obj while get all!");
		}
		
		ArrayList<String> urlIds = new ArrayList<String>();
		String posId = this.getAttribute("positionId");
		urlIds.add(posId);
		
		parentObj.initId(urlIds);
		return parentObj;
	}
	
	protected InfoMObject getLastObj() throws BTiaoExp {
		List<String> idStrs = def.idStrs;
		List<String> idList = new ArrayList<String>();
		for (String idStr : idStrs) {
			String value = this.getMatrix().getFirstValue(idStr);
			if (value == null || value.equals("")) {
				return null;
			}
			
			idList.add(value);
		}

		InfoMObject lastObj = null;
		try {
			lastObj = def.objClass.newInstance();
		} catch (Exception e) {
			throw new BTiaoExp(ErrCode.INTERNEL_ERROR, e, "failed to get parent obj while get all!");
		}
		
		lastObj.initId(idList);
		return lastObj;
	}
	
	private void getArg(Form form) throws BTiaoExp {
		lastId = form.getFirstValue("lastId");
		
		num = 0;
		String numStr = form.getFirstValue("num");
		try {
			num = Integer.parseInt(numStr);
		} catch (Exception e) {
			String errMsg = "get func's argument error, num="+numStr;
			throw new BTiaoExp(ErrCode.WRONG_PARAM, e, errMsg);
		}
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

	protected Def def;
	protected String lastId;
	protected int num;
	
	protected CommonMgr mgr = CommonMgr.instance();
}
