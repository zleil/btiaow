package com.btiao.tzsc.weixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btiao.tzsc.service.State;
import com.btiao.tzsc.service.StateMgr;
import com.btiao.tzsc.service.State.Info.MsgType;

public class WXPutStateMgr {
	static class PageView {
		public PageView(List<State> sts) {
			all = sts;
		}
		
		public WXMsg next() {
			return next(false);
		}
		
		public WXMsg next(boolean addSeq) {
			if (idx >= all.size()) {
				return null;
			}
			
			WXMsg.PicText ret = new WXMsg.PicText();
			
			for (int i=0; idx<all.size() && i<page_size; ++idx,++i) {
				State state = all.get(idx);
				
				WXMsg.PicText.Item item = new WXMsg.PicText.Item();

				for (State.Info info : state.infos) {
					if (item.title == null && info.t == State.Info.MsgType.text) {
						item.title = (addSeq ? ("["+(idx+1)+"]") : "") + info.content;
					}
					if (item.picUrl == null && info.t == State.Info.MsgType.pic) {
						item.picUrl = info.content;
					}
					if (item.title != null && item.picUrl != null) {
						break;
					}
					
					item.url = WXServletDispDetail.URI+state.areaId+"?stateId=" + state.id; 
				}
				
				if (item.picUrl == null && item.title == null) {
					continue;
				}
				
				ret.items.add(item);
			}
			
			return ret;
		}
		
		public boolean isEnd() {
			return idx >= all.size();
		}
		
		public int page_size = 10;
		
		private int idx;
		private List<State> all;
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
	
//	public void startPut(String name) {
//		if (!curPuts.containsKey(name)) {
//			curPuts.put(name, new State(name));
//		}
//	}
	
	public String putTextMsg(String name, String text) {
		List<State> all = StateMgr.instance(areaId).getAllStateByUserName(name);
		if (all != null && all.size() >= 8) {
			return Tip.get().reachMaxSwitch;
		}
		
		State state = this.curPuts.get(name);
		
		if (state == null) {
			state = new State(name);
			curPuts.put(name, state);
		}
		
		State.Info info = new State.Info(MsgType.text, text);
		state.infos.add(info);
		
		return Tip.get().continuePutTip;
	}
	
	public String putUrlMsg(String name, String url) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			return Tip.get().noFirstTextDescError;
		}
		
		State.Info info = new State.Info(MsgType.pic, url);
		state.infos.add(info);
		
		return Tip.get().continuePutTip;
	}
	
	public String putPhoneNum(String name, String tel) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			return Tip.get().noFirstTextDescError;
		}
		
		State.Info info = new State.Info(MsgType.phone, tel);
		state.infos.add(info);
		
		return Tip.get().continuePutTip;
	}
	
	public String cancelPut(String name) {
		curPuts.remove(name);
		return Tip.get().cancelSuccess + "\n\n" + Tip.get().helpStr;
	}
	
	public String endPut(String name) {
		if (!curPuts.containsKey(name)) {
			return "";
		}
		
		State state = this.curPuts.remove(name);
		state.areaId = this.areaId;
		
		if (state.infos.size() == 0) {
			return "";
		}
		
		boolean hasPutPhone = false;
		for (State.Info info : state.infos) {
			if (info.t == State.Info.MsgType.phone) {
				hasPutPhone = true;
				break;
			}
		}
		
		if (!hasPutPhone) {
			this.curPuts.put(name, state);
			return Tip.get().noPhoneNumDescErrorTip + "\n\n" + Tip.get().phoneNumFillHelpTip;
		}
		
		state.publishTime = System.currentTimeMillis();
		
		int total = StateMgr.instance(areaId).addState(name, state);
		
		return Tip.get().putSuccessTip + total;
	}
	
	public WXMsg returnSelfAll(String name) {
		List<State> allSelf = StateMgr.instance(areaId).getAllStateByUserName(name);
		
		PageView pv = new PageView(allSelf);
		
		return pv.next(true);
	}
	
	public String delOne(String name, int idx) {
		int ret = StateMgr.instance(areaId).delOneState(name, idx);
		if (ret >= 0) {
			return Tip.get().delStateSuccess + ret;
		} else {
			List<State> allSelf = StateMgr.instance(areaId).getAllStateByUserName(name);
			return Tip.get().delStateFailed + ((allSelf != null) ? allSelf.size() : 0);
		}
	}
	
	public WXMsg search(String name, String text) {
		if (this.curPuts.containsKey(name)) {
			WXMsg.Text msg = new WXMsg.Text();
			msg.content = Tip.get().doOtherActWhenPutErrorTip;
			
			return msg;
		}
		
		List<State> allMatched = StateMgr.instance(areaId).searchState(text);
		PageView pv = new PageView(allMatched);
		
		WXMsg ret = pv.next();
		
		if (ret != null) {
			synchronized (pvs) {
				pvs.put(name, pv);
				
				if (pv.isEnd()) {
					pvs.remove(name);
				}
			}
		}
		
		return ret;
	}
	
	public WXMsg more(String name) {
		synchronized (pvs) {
			PageView pv = pvs.get(name);
			if (pv == null) {
				WXMsg.Text msg = new WXMsg.Text();
				msg.content = Tip.get().noMoreDataTip;
				
				return msg;
			}
			
			WXMsg ret = pv.next();
			
			if (pv.isEnd()) {
				pvs.remove(name);
			}
			
			return ret;
		}
		
	}
	
	private WXPutStateMgr(long areaId) {
		this.areaId = areaId;
	}
	
	private final long areaId;
	
	private Map<String,State> curPuts = new HashMap<String,State>();
	
	private Map<String,PageView> pvs = new HashMap<String,PageView>();
}
