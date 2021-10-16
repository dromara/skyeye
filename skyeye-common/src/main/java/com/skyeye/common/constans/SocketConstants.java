package com.skyeye.common.constans;

import cn.hutool.json.JSONObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;

import java.util.HashMap;
import java.util.Map;

public class SocketConstants {
	
	public static enum MessageType {
		First(1, "上线通知", ""),
		Second(2, "下线通知", ""),
		Third(3, "获取在线名单通知", ""),
		Fourth(4, "聊天普通消息通知", ""),
		Fifth(5, "系统消息通知", ""),
		Sixth(6, "全体消息通知", ""),
		Seventh(7, "群组邀请消息通知", ""),
		Eighth(8, "隐身消息通知", ""),
		Ninth(9, "隐身上线消息通知", ""),
		Tenth(10, "搜索账号入群审核同意后通知用户加载群信息通知", ""),
		Eleventh(11, "群聊消息通知", ""),
		Twelfth(12, "退出群聊通知", ""),
		Thirteenth(13, "解散群聊通知", "");

		private int type;
		private String title;
		private String method;

		MessageType(int type, String title, String method) {
			this.type = type;
			this.title = title;
			this.method = method;
		}

		public static String getMethod(int type) {
			for (MessageType q : MessageType.values()) {
				if (q.getType() == type) {
					return q.getMethod();
				}
			}
			return "";
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}
	}
	
	/**
	 * 
	 * @Title: sendOrdinaryMsg
	 * @Description: 发送普通消息
	 * @param jsonObject
	 * @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	public static Map<String, Object> sendOrdinaryMsg(JSONObject jsonObject){
		Map<String, Object> map = new HashMap<>();
		map.put("avatar", jsonObject.getStr("avatar"));//头像
		map.put("textMessage", jsonObject.getStr("message"));//消息
		map.put("username", jsonObject.getStr("username"));//收件人名称
		map.put("fromId", jsonObject.getStr("userId"));//发件人id
		map.put("toId", jsonObject.getStr("to"));//收件人id
		map.put("createTime", DateUtil.getTimeAndToString());
		map.put("dataId", ToolUtil.getSurFaceId());
		return map;
	}
	
	/**
	 * 
	 * @Title: sendAllPeopleMsg
	 * @Description: 发送全体消息
	 * @param jsonObject
	 * @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	public static Map<String, Object> sendAllPeopleMsg(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<>();
		map.put("avatar", jsonObject.getStr("avatar"));//头像
		map.put("textMessage", jsonObject.getStr("message"));//消息
		map.put("username", jsonObject.getStr("username"));//收件人名称
		map.put("fromId", jsonObject.getStr("userId"));//发件人id
		map.put("tousername", "所有人");
		return map;
	}
	
	/**
	 * 
	 * @Title: sendGroupTalkPeopleMsg
	 * @Description: 发送群组聊天消息
	 * @param jsonObject
	 * @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	public static Map<String, Object> sendGroupTalkPeopleMsg(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<>();
		map.put("avatar", jsonObject.getStr("avatar"));//头像
		map.put("textMessage", jsonObject.getStr("message"));//消息
		map.put("username", jsonObject.getStr("username"));//发消息人名称
		map.put("id", jsonObject.getStr("to"));//收件人id，在此处为群聊id
		map.put("userId", jsonObject.getStr("userId"));//群聊消息不发送给自己
		return map;
	}
	
	/**
	 * 
	 * @Title: sendAgreeJoinGroupMsg
	 * @Description: 搜索账号入群审核同意后通知用户加载群信息
	 * @param jsonObject
	 * @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	public static Map<String, Object> sendAgreeJoinGroupMsg(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<>();
		map.put("avatar", jsonObject.getStr("avatar"));//头像
		map.put("groupname", jsonObject.getStr("groupname"));//群聊名称
		map.put("id", jsonObject.getStr("id"));//群聊id
		map.put("toId", jsonObject.getStr("to"));//收件人id
		return map;
	}
	
	/**
	 * 
	 * @Title: sendOutGroupToCreaterMsg
	 * @Description: 退出群聊--创建人接收消息
	 * @param jsonObject
	 * @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	public static Map<String, Object> sendOutGroupToCreaterMsg(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<>();
		map.put("groupId", jsonObject.getStr("to"));//收件人id，在此处为群聊id
		map.put("userName", jsonObject.getStr("userName"));//退群人名称
		map.put("userId", jsonObject.getStr("userId"));//群聊消息不发送给自己
		return map;
	}
	
	/**
	 * 
	 * @Title: sendDisbandGroupToAllMsg
	 * @Description: 解散群聊--所有人接收消息
	 * @param jsonObject
	 * @return
	 * @return: Map<String,Object>
	 * @throws
	 */
	public static Map<String, Object> sendDisbandGroupToAllMsg(JSONObject jsonObject) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", jsonObject.getStr("to"));//收件人id，在此处为群聊id
		map.put("userName", jsonObject.getStr("userName"));//群主
		map.put("userId", jsonObject.getStr("userId"));//群主id
		return map;
	}

}
