package com.btiao.tzsc.weixin;

import java.util.ArrayList;
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
		
		public List<WXMsg> next() {
			List<WXMsg> rets = new ArrayList<WXMsg>();
//			for (int i=0; idx<all.size()&&i<page_size; ++idx,++i) {
//				WXMsg.PicText msg = genWXMsg(all.get(idx));
//				rets.add(msg);
//			}
			
			WXMsg.PicText ret = new WXMsg.PicText();
			
			for (int i=0; idx<all.size() && i<page_size; ++idx,++i) {
				State state = all.get(idx);
				
				WXMsg.PicText.Item item = new WXMsg.PicText.Item();

				for (State.Info info : state.infos) {
					if (item.title == null && info.t == State.Info.MsgType.text) {
						item.title = info.content;
					}
					if (item.picUrl == null && info.t == State.Info.MsgType.pic) {
						item.picUrl = info.content;
					}
					if (item.title != null && item.picUrl != null) {
						break;
					}
					
					item.url = "http://182.92.81.56/btiao/tzsc/wx_dispDetail/65536?stateId=" + state.id; 
				}
				
				if (item.picUrl == null && item.title == null) {
					continue;
				}
				
				ret.items.add(item);
			}
			
			if (ret.items.size() != 0) {
				rets.add(ret);
			}
			
			return rets;
		}
		
		public boolean isEnd() {
			return idx >= all.size();
		}
		
		private WXMsg.PicText genWXMsg(State state) {
			WXMsg.PicText msg = new WXMsg.PicText();
			
			WXMsg.PicText.Item item = new WXMsg.PicText.Item();
			for (int i=0; i<state.infos.size();) {
				if (state.infos.get(i).t == State.Info.MsgType.text) {
					item.title = state.infos.get(i).content;
					
					if ((i+1) >= state.infos.size()) {
						msg.items.add(item);
						break;
					}
					
					++i;
					
					if (state.infos.get(i).t == State.Info.MsgType.pic) {
						item.picUrl = state.infos.get(i).content;
						item.url = item.picUrl;
						//item.desc = item.title;
						msg.items.add(item);
						
						item = new WXMsg.PicText.Item();
						
						++i;
						continue;
					} else {
						do {
							if (item.desc == null) {
								item.desc = "";
							}
							
							item.desc += state.infos.get(i).content + "；";
							
							if ((i+1) >= state.infos.size()) {
								msg.items.add(item);
								break;
							}
							
							++i;
							
							if (state.infos.get(i).t == State.Info.MsgType.pic) {
								item.picUrl = state.infos.get(i).content;
								//item.url = item.picUrl;
								msg.items.add(item);
								
								item = new WXMsg.PicText.Item();
								break;
							}
						} while (true);
						
						++i;
						continue;
					}
				} else {
					item.title = "啥也别说了，看照片:-)";//state.infos.get(i).content;
					item.picUrl = state.infos.get(i).content;
					//item.url = item.title;
					//item.desc = item.title;
					
					msg.items.add(item);
					
					item = new WXMsg.PicText.Item();
					++i;
				}
			}
			
			return msg;
		}
		
		public int page_size = 10;
		
		private int idx;
		private List<State> all;
	}
	
	static private WXPutStateMgr inst;
	
	static synchronized WXPutStateMgr instance() {
		if (inst == null) {
			inst = new WXPutStateMgr();
		}
		
		return inst;
	}
	
//	public void startPut(String name) {
//		if (!curPuts.containsKey(name)) {
//			curPuts.put(name, new State(name));
//		}
//	}
	
	public String putTextMsg(String name, String text) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			state = new State(name);
			curPuts.put(name, state);
		}
		
		State.Info info = new State.Info(MsgType.text, text);
		state.infos.add(info);
		
		return "发送文字、图片，继续描述\n\n" + 
			"发送数字 0 ，取消描述\n" +
			"发送数字 1 ，提交物品信息";
	}
	
	public String putUrlMsg(String name, String url) {
		State state = this.curPuts.get(name);
		
		if (state == null) {
			return "亲，得先用文字描述下物品呦";
		}
		
		State.Info info = new State.Info(MsgType.pic, url);
		state.infos.add(info);
		
		return "发送文字、图片，继续描述物品\n\n" + "发送数字 0 ，取消描述\n" +
		"发送数字 1 ，提交物品描述";
	}
	
	public String cancelPut(String name) {
		curPuts.remove(name);
		return "取消成功\n\n" + WXMsgProcessor.helpStr;
	}
	
	public String endPut(String name) {
		if (!curPuts.containsKey(name)) {
			return "";
		}
		
		State state = this.curPuts.remove(name);
		if (state.infos.size() == 0) {
			return "";
		}
		
		state.publishTime = System.currentTimeMillis();
		
		int total = StateMgr.instance().addState(name, state);
		
		return "您又多了件物品，候着邻居来买吧\n\n您当前有"+total+"件物品";
	}
	
	public String returnSelfAll(String name) {
		StringBuilder sb = new StringBuilder();
		List<State> allSelf = StateMgr.instance().getAllStateByUserName(name);
		
		if (allSelf == null || allSelf.size() == 0) {
			return "您当前还没有任何物品:-)\n\n" + WXMsgProcessor.helpStr;
		}
		
		sb.append("您当前有"+allSelf.size()+"件物品：\n");
		
		for (int i=1; i<=allSelf.size(); ++i) {
			sb.append(i);
			sb.append(" ");
			String desc = allSelf.get(i-1).infos.get(0).content;			
			sb.append(desc);
			
			sb.append("\n");
		}
		
		sb.append("\n发送数字5 x，可细看您的第x个物品");
		
		return sb.toString();
	}
	
	public String delOne(String name, int idx) {
		int ret = StateMgr.instance().delOneState(name, idx);
		if (ret >= 0) {			
			return "删除成功\n您当前还有"+ret+"件物品";
		} else {
			List<State> allSelf = StateMgr.instance().getAllStateByUserName(name);
			return "您要删除的物品"+idx+"不存在\n您当前有"+((allSelf != null) ? allSelf.size() : 0)+"件待交换物品";
		}
	}
	
	public List<WXMsg> search(String name, String text) {
		if (this.curPuts.containsKey(name)) {
			WXMsg.Text msg = new WXMsg.Text();
			msg.content = "您正在描述物品，请先取消或提交，再进行搜索\n\n"+
					"发送数字 0 ，取消描述\n" + 
					"发送数字 1 ，提交物品信息";
			
			List<WXMsg> ret = new ArrayList<WXMsg>();
			ret.add(msg);
			return ret;
		}
		
		List<State> allMatched = StateMgr.instance().searchState(text);
		PageView pv = new PageView(allMatched);
		
		List<WXMsg> ret = pv.next();
		
		synchronized (pvs) {
			pvs.put(name, pv);
			
			if (pv.isEnd()) {
				pvs.remove(name);
			}
		}
		
		return ret;
	}
	
	public List<WXMsg> more(String name) {
		synchronized (pvs) {
			PageView pv = pvs.get(name);
			if (pv == null) {
				WXMsg.Text msg = new WXMsg.Text();
				msg.content = "没有更多的信息";
				
				List<WXMsg> ret = new ArrayList<WXMsg>();
				ret.add(msg);
				return ret;
			}
			
			List<WXMsg> ret = pv.next();
			
			if (pv.isEnd()) {
				pvs.remove(name);
			}
			
			return ret;
		}
		
	}
	
	private Map<String,State> curPuts = new HashMap<String,State>();
	
	private Map<String,PageView> pvs = new HashMap<String,PageView>();
}
