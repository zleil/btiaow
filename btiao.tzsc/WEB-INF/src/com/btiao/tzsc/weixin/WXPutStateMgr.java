package com.btiao.tzsc.weixin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.btiao.tzsc.service.ComDataMgr;
import com.btiao.tzsc.service.GlobalParam;
import com.btiao.tzsc.service.MetaDataId;
import com.btiao.tzsc.service.MyLogger;
import com.btiao.tzsc.service.Tip;
import com.btiao.tzsc.service.UserInfo;
import com.btiao.tzsc.service.WPState;
import com.btiao.tzsc.service.StateMgr;
import com.btiao.tzsc.service.WPState.Info.MsgType;
import com.btiao.tzsc.weixin.WXMsg.PicText;

public class WXPutStateMgr {
	static class PageView {
		public PageView(List<WPState> sts) {
			all = sts;
		}
		
		public PageView(List<WPState> sts, int page_size) {
			all = sts;
			this.page_size = page_size;
		}
		
		public WXMsg next() {
			return next(true);
		}
		
		public WXMsg next(boolean addSeq) {
			this.accessTime = System.currentTimeMillis();
			
			if (idx >= all.size()) {
				return null;
			}
			
			WXMsg.PicText ret = new WXMsg.PicText();
			
			for (int i=0; idx<all.size() && i<page_size; ++idx,++i) {
				WPState state = all.get(idx);
				
				WXMsg.PicText.Item item = new WXMsg.PicText.Item();

				for (WPState.Info info : state.getInfos()) {
					if (item.title == null && info.t == WPState.Info.MsgType.text) {
						item.title = (addSeq ? ("【"+(idx+1)+"】") : "") + info.content;
					}
					if (item.picUrl == null && info.t == WPState.Info.MsgType.pic) {
						item.picUrl = info.content;
					}
					if (item.title != null && item.picUrl != null) {
						break;
					}
					
					String goUrl = null;
					try {
						goUrl = URLEncoder.encode("http://"+WXApiSession.serverip+"/btiao/tzsc/wx_managemine/" + state.areaId + "?act=dispstate&stateId="+state.id+"&areaId="+state.areaId, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						MyLogger.get().error(e);
					}
					item.url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+WXApiSession.appId+"&redirect_uri="+goUrl+"&response_type=code&scope=snsapi_base#wechat_redirect";
				}
				
				if (item.picUrl == null && item.title == null) {
					continue;
				}
				
				ret.items.add(item);
			}
			
			return ret;
		}
		
		public int getTotal() {
			return all.size();
		}
		
		public boolean isEnd() {
			return idx >= all.size();
		}
		
		public int page_size = 10;
		
		private int idx;
		private List<WPState> all;
		private long accessTime;
	}
	
	static private Map<Long,WXPutStateMgr> insts = new HashMap<Long,WXPutStateMgr>();
	
	static synchronized WXPutStateMgr instance(long areaId) {
		WXPutStateMgr inst = insts.get(areaId);
		if (inst == null) {
			inst = new WXPutStateMgr(areaId);
			insts.put(areaId, inst);
		}
		
		return inst;
	}
	
	public synchronized String putTextMsg(String name, String text) {
		String shouldRet = checkPut(name);
		if (shouldRet != null) {
			return shouldRet;
		}
		
		text = normalize(text);
		
		WPState state = this.curPuts.get(name);
		
		if (state == null) {
			state = new WPState(name);
			curPuts.put(name, state);
		}
		
		WPState.Info info = new WPState.Info(MsgType.text, text);
		state.getInfos().add(info);
		
		//每次提交元描述信息后，这个时间都要更新下，这样timeoutDelCurState线程会重新计算超时时间
		state.publishTime = System.currentTimeMillis();
		
		return Tip.get().continuePutTip;
	}
	
	public synchronized String putPicUrlMsg(String name, String url) {
		String shouldRet = checkPut(name);
		if (shouldRet != null) {
			return shouldRet;
		}
		
		WPState state = this.curPuts.get(name);
		
		if (state == null) {
			state = new WPState(name);
			curPuts.put(name, state);
		}
		
		WPState.Info info = new WPState.Info(MsgType.pic, url);
		state.getInfos().add(info);
		
		//每次提交元描述信息后，这个时间都要更新下，这样timeoutDelCurState线程会重新计算超时时间
		state.publishTime = System.currentTimeMillis();
		
		return Tip.get().continuePutTip;
	}
	
	public synchronized String cancelPut(String name) {
		curPuts.remove(name);
		return Tip.get().cancelSuccess;
	}
	
	public synchronized String endPut(String name) {
		String shouldRet = checkPut(name);
		if (shouldRet != null) {
			return shouldRet;
		}
		
		if (!curPuts.containsKey(name)) {
			return Tip.get().notDescInPublish;
		}
		
		WPState state = this.curPuts.remove(name);
		state.areaId = this.areaId;
		
		if (state.getInfos().size() == 0) {
			return Tip.get().notDescInPublish;
		}
		
		state.headFirst();
		
		ComDataMgr<UserInfo> uinfomgr = ComDataMgr.<UserInfo>instance(UserInfo.class.getSimpleName(), this.areaId);
		UserInfo uinfo = uinfomgr.get(state.userId);
		
		WPState.Info phoneinfo = new WPState.Info(MsgType.phone, uinfo.telId);
		state.getInfos().add(phoneinfo);
		
		state.publishTime = System.currentTimeMillis();
		
		int total = StateMgr.instance(areaId).addState(name, state);
		
		return Tip.get().putSuccessTip + total;
	}
	
	public synchronized WXMsg getall(String name) {		
		List<WPState> allMatched = StateMgr.instance(areaId).searchState("");
		PageView pv = new PageView(allMatched, 9);
		
		WXMsg.PicText ret = (PicText) pv.next();
		
		if (ret != null) {
			synchronized (pvs) {
				pvs.put(name, pv);
				
				if (pv.isEnd()) {
					pvs.remove(name);
				}
				
				insertLastItemTip(ret, pv);
			}
		}
		
		return ret;
	}
	
	private void insertLastItemTip(WXMsg.PicText msg, PageView pv) {
		if (pv.isEnd()) {
			WXMsg.PicText.Item item = new WXMsg.PicText.Item();
			item.title = Tip.get().allReturnedTip + pv.getTotal();
			msg.items.add(item);
		} else {
			WXMsg.PicText.Item item = new WXMsg.PicText.Item();
			item.title = Tip.get().moreTip;
			msg.items.add(item);
		}
	}
	
	public WXMsg more(String name) {
		synchronized (pvs) {
			PageView pv = pvs.get(name);
			if (pv == null) {
				return null;
			}
			
			WXMsg ret = pv.next();
			
			if (pv.isEnd()) {
				pvs.remove(name);
			}
			
			insertLastItemTip((PicText)ret, pv);
			
			return ret;
		}
		
	}
	
	private WXPutStateMgr(final long areaId) {
		this.areaId = areaId;
		
		Thread timeoutDelCurState = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(GlobalParam.wxputstate_circle);
						
						WXPutStateMgr inst = WXPutStateMgr.instance(areaId);
						synchronized (inst) {
							List<String> timeoutUsrs = new ArrayList<String>();
							
							//处理提交待描述物品的缓存信息，超时后发送知会用户的消息
							MyLogger.get().info("timeout for curPuts, areaId:"+areaId);
							for (Entry<String,WPState> entry : inst.curPuts.entrySet()) {
								String usrId = entry.getKey();
								WPState state = entry.getValue();
								
								if (isStateTimeout(state)) {
									timeoutUsrs.add(usrId);
								}
							}
							
							for (String usrId : timeoutUsrs) {
								inst.curPuts.remove(usrId);
								new WXApi().sendWXTxtMsg(usrId, Tip.get().curPublishTimeout);
							}
							
							//处理随便查看的分页缓存信息的超时，超时后不发送知会用户的消息
							timeoutUsrs.clear();
							MyLogger.get().info("timeout for pageview, areaId:"+areaId);
							for (Entry<String,PageView> entry : inst.pvs.entrySet()) {
								String usrId = entry.getKey();
								PageView pv = entry.getValue();
								
								if (isPageViewTimeout(pv)) {
									timeoutUsrs.add(usrId);
								}
							}
						}
					} catch (Throwable e) {
						MyLogger.get().error("get a error in timeoutDelCurState thread!", e);
					}
				}
			}
			
			boolean isStateTimeout(WPState state) {
				long time = System.currentTimeMillis() - state.publishTime;
				return time > GlobalParam.wxputstate_statetimeout; // one day timeout
			}
			
			boolean isPageViewTimeout(PageView pv) {
				long time = System.currentTimeMillis() - pv.accessTime;
				return time > GlobalParam.wxputstate_pageviewimeout; // one day timeout
			}
			
			
		};
		timeoutDelCurState.setName("timeoutDelCurState_"+areaId);
		timeoutDelCurState.start();
	}
	
	private String checkPut(String name) {
		ComDataMgr<UserInfo> uinfomgr = ComDataMgr.<UserInfo>instance(UserInfo.class.getSimpleName(), this.areaId);
		ComDataMgr<UserInfo> dengjimgr = ComDataMgr.<UserInfo>instance(MetaDataId.dengji, this.areaId);
		UserInfo uinfo = uinfomgr.get(name);
		if (uinfo == null) {
			if (dengjimgr.get(name) == null) {
				return Tip.get().notDengji;
			} else {
				return Tip.get().dengjiAndNotPass;
			}
		}
		
		List<WPState> all = StateMgr.instance(areaId).getAllStateByUserName(name);
		if (all != null && all.size() >= GlobalParam.max_switch_wuping) {
			return Tip.get().reachMaxSwitch;
		}
		
		return null;
	}
	
	private String normalize(String text) {
		text = Normalizer.normalize(text, Normalizer.Form.NFD);
		
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<text.length(); ++i) {
			char ch = text.charAt(i);
			if (ch == '<') {
				sb.append("&lt;");
			} else if (ch == '>') {
				sb.append("&gt;");
			} else {
				sb.append(ch);
			}
		}
		
		return sb.toString();
	}
	
	private final long areaId;
	
	//为每个用户存放待提交物品信息
	private Map<String,WPState> curPuts = new HashMap<String,WPState>();
	
	//为每个用户存放分页浏览的信息
	private Map<String,PageView> pvs = new HashMap<String,PageView>();
}
