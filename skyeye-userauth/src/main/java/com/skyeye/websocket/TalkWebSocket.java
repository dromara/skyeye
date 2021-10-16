/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.dao.CompanyTalkGroupDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @ClassName: TalkWebSocket
 * @Description: 聊天/消息推送
 * @author: skyeye云系列--卫志强
 * @date: 2020年11月14日 下午9:36:38
 *   
 * @Copyright: 2020 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Component
@ServerEndpoint("/talkwebsocket/{userId}")
public class TalkWebSocket {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TalkWebSocket.class);
	
	/**
	 * 在线人数
	 */
	public static int onlineNumber = 0;
	/**
	 * 以用户的姓名为key，WebSocket为对象保存起来
	 */
	private static Map<String, TalkWebSocket> clients = new ConcurrentHashMap<String, TalkWebSocket>();
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
		if(clients.containsKey(userId)){
			return;
		}
		onlineNumber++;
		LOGGER.info("现在来连接的客户id: {}, 用户名: {}", session.getId(), userId);
		this.userId = userId;
		this.session = session;
		LOGGER.info("有新连接加入！ 当前在线人数: {}", onlineNumber);
		try {
			// 先给所有人发送通知，说我上线了
			Map<String, Object> map1 = new HashMap<>();
			map1.put("messageType", SocketConstants.MessageType.First.getType());
			map1.put("userId", userId);
			sendMessageAll(JSONUtil.toJsonStr(map1), userId);

			// 把自己的信息加入到map当中去
			clients.put(userId, this);
			// 给自己发一条消息：告诉自己现在都有谁在线
			Map<String, Object> map2 = new HashMap<>();
			map2.put("messageType", SocketConstants.MessageType.Third.getType());
			map2.put("onlineUsers", clients.keySet());
			sendMessageTo(JSONUtil.toJsonStr(map2), userId);
		} catch (IOException e) {
			LOGGER.warn("{}上线的时候通知所有人发生了错误", userId);
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		LOGGER.warn("服务端发生了错误: {}", error.getMessage());
	}

	/**
	 * 连接关闭
	 */
	@OnClose
	public void onClose() {
		if(!ToolUtil.isBlank(userId) && clients.containsKey(userId)){
			onlineNumber--;
			clients.remove(userId);
			try {
				Map<String, Object> map1 = new HashMap<>();
				map1.put("messageType", 2);
				map1.put("onlineUsers", clients.keySet());
				map1.put("userId", userId);
				sendMessageAll(JSONUtil.toJsonStr(map1), userId);
			} catch (IOException e) {
				LOGGER.warn(userId + "下线的时候通知所有人发生了错误");
			}
			LOGGER.info("有连接关闭！ 当前在线人数" + onlineNumber);
		}
	}

	/**
	 * 收到客户端的消息
	 *
	 * @param message 消息
	 * @param session 会话
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		try {
			LOGGER.info("来自客户端消息: {}, 客户端的id是: {}", message, session.getId());
			JSONObject jsonObject = JSONUtil.toBean(message, null);
			int type = jsonObject.getInt("type");
			Map<String, Object> map1 = new HashMap<>();
			map1.put("messageType", type);
			if(SocketConstants.MessageType.Fourth.getType() == type){//普通消息
				map1 = SocketConstants.sendOrdinaryMsg(jsonObject);
				CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
				companyTalkGroupDao.insertPersonToPersonMessage(map1);
				sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
			}else if(SocketConstants.MessageType.Fifth.getType() == type){//系统消息
				
			}else if(SocketConstants.MessageType.Sixth.getType() == type){//全体消息
				map1 = SocketConstants.sendAllPeopleMsg(jsonObject);
				sendMessageAll(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
			}else if(SocketConstants.MessageType.Seventh.getType() == type){//群组邀请消息
				map1.put("toId", jsonObject.getStr("to"));//收件人id
				sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
			}else if(SocketConstants.MessageType.Eighth.getType() == type){//隐身消息
				map1.put("userId", jsonObject.getStr("userId"));
				sendMessageAll(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
			}else if(SocketConstants.MessageType.Ninth.getType() == type){//隐身上线消息
				map1.put("userId", jsonObject.getStr("userId"));
				sendMessageAll(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
			}else if(SocketConstants.MessageType.Tenth.getType() == type){//搜索账号入群审核同意后通知用户加载群信息
				map1 = SocketConstants.sendAgreeJoinGroupMsg(jsonObject);
				sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("to"));
			}else if(SocketConstants.MessageType.Eleventh.getType() == type){//群聊
				map1 = SocketConstants.sendGroupTalkPeopleMsg(jsonObject);
				CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
				Map<String, Object> groupState = companyTalkGroupDao.queryGroupStateById(map1);
				if("1".equals(groupState.get("state").toString())){//正常
					//插入消息记录
					map1.put("createTime", DateUtil.getTimeAndToString());
					map1.put("dataId", ToolUtil.getSurFaceId());
					companyTalkGroupDao.insertPersonToGroupMessage(map1);
					sendMessageToThisGroupMember(map1);
				}else{
					map1.clear();
					map1.put("messageType", "1301");
					map1.put("groupId", jsonObject.getStr("to"));//收件人id，在此处为群聊id
					sendMessageTo(JSONUtil.toJsonStr(map1), jsonObject.getStr("userId"));
				}
			}else if(SocketConstants.MessageType.Twelfth.getType() == type){//退出群聊--创建人接收消息
				map1 = SocketConstants.sendOutGroupToCreaterMsg(jsonObject);
				CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
				Map<String, Object> groupMation = companyTalkGroupDao.queryGroupCreateIdById(map1);
				map1.put("toId", groupMation.get("createId"));//收件人id
				sendMessageTo(JSONUtil.toJsonStr(map1), groupMation.get("createId").toString());
			}else if(SocketConstants.MessageType.Thirteenth.getType() == type){//解散群聊--所有人接收消息
				map1 = SocketConstants.sendDisbandGroupToAllMsg(jsonObject);
				sendMessageToThisGroupMember(map1);
			}
		} catch (Exception e) {
			LOGGER.warn("发生了错误了: {}", e);
		}
	}
	
	/**
	 * 
	 * @Title: sendMessageToThisGroupMember
	 * @Description: 发送消息给当前群的所有人
	 * @param map
	 * @throws Exception
	 * @return: void
	 * @throws
	 */
	private void sendMessageToThisGroupMember(Map<String, Object> map) throws Exception{
		CompanyTalkGroupDao companyTalkGroupDao = SpringUtils.getBean(CompanyTalkGroupDao.class);
		List<Map<String, Object>> members = companyTalkGroupDao.queryGroupMemberByGroupIdAndNotThisUser(map);
		if(members.size() > 0){
			for(Map<String, Object> member : members){
				map.put("toId", member.get("id"));//收件人id
				sendMessageTo(JSONUtil.toJsonStr(map), member.get("id").toString());
			}
		}
	}

	/**
	 * 
	     * @Title: sendMessageTo
	     * @Description: 发送给指定用户消息
	     * @param message
	     * @param userId
	     * @throws IOException    参数
	     * @return void    返回类型
	     * @throws
	 */
	public void sendMessageTo(String message, String userId) throws IOException {
		TalkWebSocket item = clients.get(userId);
		if(item != null){
			item.session.getAsyncRemote().sendText(message);
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
		for (TalkWebSocket item : clients.values()) {
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
