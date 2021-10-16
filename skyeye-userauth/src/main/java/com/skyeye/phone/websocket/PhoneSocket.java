/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.phone.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CompanyTalkGroupDao;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
     * @ClassName: PhoneSocket
     * @Description: 手机端消息
     * @author 卫志强
     * @date 2020年3月19日
     *
 */
@Component
@ServerEndpoint("/phonetalkwebsocket/{userId}")
public class PhoneSocket {
	
	/**
	 * 在线人数
	 */
	public static int onlineNumber = 0;
	/**
	 * 以用户的姓名为key，WebSocket为对象保存起来
	 */
	private static Map<String, PhoneSocket> clients = new ConcurrentHashMap<String, PhoneSocket>();
	/**
	 * 会话
	 */
	private Session session;
	/**
	 * 用户id
	 */
	private String userId;
	
	/**
	 * 建立连接
	 *
	 * @param session
	 */
	@OnOpen
	public void onOpen(@PathParam("userId") String userId, Session session) {
		//判断该用户是否已经注册
		if(!clients.containsKey(userId)){
			onlineNumber++;
			System.out.println("手机端：现在来连接的客户id：" + session.getId() + "用户名：" + userId);
			this.userId = userId;
			this.session = session;
			System.out.println("手机端：有新连接加入！ 当前在线人数" + onlineNumber);
			try {
				// messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息 5系统消息 6全体消息 7群组邀请消息 8隐身消息 9隐身上线消息 10搜索账号入群审核同意后通知用户加载群信息 11群聊消息
				//12 退出群聊 13解散群聊
				// 先给所有人发送通知，说我上线了
				Map<String, Object> map1 = new HashMap<>();
				map1.put("messageType", 1);
				map1.put("userId", userId);
				sendMessageAll(JSONUtil.toJsonStr(map1), userId);

				// 把自己的信息加入到map当中去
				clients.put(userId, this);
				// 给自己发一条消息：告诉自己现在都有谁在线
				Map<String, Object> map2 = new HashMap<>();
				map2.put("messageType", 3);
				// 移除掉自己
				Set<String> set = clients.keySet();
				map2.put("onlineUsers", set);
				sendMessageTo(JSONUtil.toJsonStr(map2), userId);
			} catch (IOException e) {
				System.out.println(userId + "上线的时候通知所有人发生了错误");
			}
		}

	}

	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("手机端：服务端发生了错误" + error.getMessage());
	}

	/**
	 * 连接关闭
	 */
	@OnClose
	public void onClose() {
		if(!ToolUtil.isBlank(userId)){
			onlineNumber--;
			clients.remove(userId);
			try {
				// messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息 5系统消息 6全体消息 7群组邀请消息 8隐身消息 9隐身上线消息 10搜索账号入群审核同意后通知用户加载群信息 11群聊消息
				//12 退出群聊 13解散群聊
				Map<String, Object> map1 = new HashMap<>();
				map1.put("messageType", 2);
				map1.put("onlineUsers", clients.keySet());
				map1.put("userId", userId);
				sendMessageAll(JSONUtil.toJsonStr(map1), userId);
			} catch (IOException e) {
				System.out.println(userId + "下线的时候通知所有人发生了错误");
			}
			System.out.println("手机端：有连接关闭！ 当前在线人数" + onlineNumber);
		}
	}

	/**
	 * 收到客户端的消息
	 *
	 * @param message
	 *            消息
	 * @param session
	 *            会话
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			System.out.println("来自客户端消息：" + message + "客户端的id是：" + session.getId());
			JSONObject jsonObject = JSONUtil.toBean(message, null);
			String type = jsonObject.getStr("type");
			// messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息 5系统消息 6全体消息 7群组邀请消息 8隐身消息 9隐身上线消息 10搜索账号入群审核同意后通知用户加载群信息 11群聊消息
			//12 退出群聊 13解散群聊
			Map<String, Object> map1 = new HashMap<>();
			if("4".equals(type)){//普通消息
				map1.put("messageType", type);
				map1.put("avatar", jsonObject.getStr("avatar"));//头像
				map1.put("textMessage", jsonObject.getStr("message"));//消息
				map1.put("username", jsonObject.getStr("username"));//收件人名称
				map1.put("fromId", jsonObject.getStr("userId"));//发件人id
				map1.put("toId", jsonObject.getStr("to"));//收件人id
				CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
				map1.put("createTime", DateUtil.getTimeAndToString());
				map1.put("dataId", ToolUtil.getSurFaceId());
				companyTalkGroupDao.insertPersonToPersonMessage(map1);
				sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
			}else if("5".equals(type)){//系统消息
				
			}else if("6".equals(type)){//全体消息
				map1.put("messageType", type);
				map1.put("avatar", jsonObject.getStr("avatar"));//头像
				map1.put("textMessage", jsonObject.getStr("message"));//消息
				map1.put("username", jsonObject.getStr("username"));//收件人名称
				map1.put("fromId", jsonObject.getStr("userId"));//发件人id
				map1.put("tousername", "所有人");
				sendMessageAll(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
			}else if("7".equals(type)){//群组邀请消息
				map1.put("messageType", type);
				map1.put("toId", jsonObject.getStr("to"));//收件人id
				sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
			}else if("8".equals(type)){//隐身消息
				map1.put("messageType", type);
				map1.put("userId", jsonObject.getStr("userId"));
				sendMessageAll(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
			}else if("9".equals(type)){//隐身上线消息
				map1.put("messageType", type);
				map1.put("userId", jsonObject.getStr("userId"));
				sendMessageAll(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
			}else if("10".equals(type)){//搜索账号入群审核同意后通知用户加载群信息
				map1.put("messageType", type);
				map1.put("avatar", jsonObject.getStr("avatar"));//头像
				map1.put("groupname", jsonObject.getStr("groupname"));//群聊名称
				map1.put("id", jsonObject.getStr("id"));//群聊id
				map1.put("toId", jsonObject.getStr("to"));//收件人id
				sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
			}else if("11".equals(type)){//群聊
				map1.put("messageType", type);
				map1.put("avatar", jsonObject.getStr("avatar"));//头像
				map1.put("textMessage", jsonObject.getStr("message"));//消息
				map1.put("username", jsonObject.getStr("username"));//发消息人名称
				map1.put("id", jsonObject.getStr("to"));//收件人id，在此处为群聊id
				map1.put("userId", jsonObject.getStr("userId"));//群聊消息不发送给自己
				CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
				Map<String, Object> groupState = companyTalkGroupDao.queryGroupStateById(map1);
				if("1".equals(groupState.get("state").toString())){//正常
					//插入消息记录
					map1.put("createTime", DateUtil.getTimeAndToString());
					map1.put("dataId", ToolUtil.getSurFaceId());
					companyTalkGroupDao.insertPersonToGroupMessage(map1);
					List<Map<String, Object>> members = companyTalkGroupDao.queryGroupMemberByGroupIdAndNotThisUser(map1);
					if(members.size() > 0){
						for(Map<String, Object> member : members){
							map1.put("toId", member.get("id"));//收件人id
							sendMessageTo(JSONUtil.toJsonStr(map1), member.get("id").toString());
						}
					}
				}else{
					map1.clear();
					map1.put("messageType", "1301");
					map1.put("groupId", jsonObject.getStr("to"));//收件人id，在此处为群聊id
					sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
				}
			}else if("12".equals(type)){//退出群聊--创建人接收消息
				map1.put("messageType", type);
				map1.put("groupId", jsonObject.getStr("to"));//收件人id，在此处为群聊id
				map1.put("userName", jsonObject.getStr("userName"));//退群人名称
				map1.put("userId", jsonObject.getStr("userId"));//群聊消息不发送给自己
				CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
				Map<String, Object> groupMation = companyTalkGroupDao.queryGroupCreateIdById(map1);
				map1.put("toId", groupMation.get("createId"));//收件人id
				sendMessageTo(JSONUtil.toJsonStr(map1), groupMation.get("createId").toString());
			}else if("13".equals(type)){//解散群聊--所有人接收消息
				map1.put("messageType", type);
				map1.put("id", jsonObject.getStr("to"));//收件人id，在此处为群聊id
				map1.put("userName", jsonObject.getStr("userName"));//群主
				map1.put("userId", jsonObject.getStr("userId"));//群主id
				CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
				List<Map<String, Object>> members = companyTalkGroupDao.queryGroupMemberByGroupIdAndNotThisUser(map1);
				if(members.size() > 0){
					for(Map<String, Object> member : members){
						map1.put("toId", member.get("id"));//收件人id
						sendMessageTo(JSONUtil.toJsonStr(map1), member.get("id").toString());
					}
				}
			}
		} catch (Exception e) {
			System.out.println("发生了错误了");
		}
	}

	/**
	 * 
	     * @Title: sendMessageTo
	     * @Description: 发送给指定用户消息
	     * @param message
	     * @param ToUserName
	     * @throws IOException    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void sendMessageTo(String message, String ToUserName) throws IOException {
		for (PhoneSocket item : clients.values()) {
			if (item.userId.equals(ToUserName)) {
				item.session.getAsyncRemote().sendText(message);
				break;
			}
		}
	}

	/**
	 * 
	     * @Title: sendMessageAll
	     * @Description: 发送给全部用户消息
	     * @param message
	     * @param FromUserName
	     * @throws IOException    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void sendMessageAll(String message, String FromUserName) throws IOException {
		for (PhoneSocket item : clients.values()) {
			item.session.getAsyncRemote().sendText(message);
		}
	}
	
	/**
	 * 获取当前在线的用户id
	 * @return
	 */
	public static Set<String> getOnlineUserId(){
		return clients.keySet();
	}

	public static synchronized int getOnlineCount() {
		return onlineNumber;
	}

}
