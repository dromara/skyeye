/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName: AiMessageWebSocket
 * @Description: AI消息WebSocket
 * @author: skyeye云系列--卫志强
 * @date: 2020年11月14日 下午9:36:38
 * @Copyright: 2020 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Component
@ServerEndpoint("/aiMessageWebSocket/{userId}")
public class AiMessageWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiMessageWebSocket.class);

    /**
     * 在线人数
     */
    public static int onlineNumber = 0;

    /**
     * 以用户的姓名为key，WebSocket为对象保存起来
     */
    private static Map<String, AiMessageWebSocket> clients = new ConcurrentHashMap<String, AiMessageWebSocket>();
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
        this.userId = userId;
        this.session = session;
        AiMessageWebSocket old = clients.put(userId, this);
        if (old == null) {
            onlineNumber++;
            LOGGER.info("AI WebSocket 接入 userId={}, session={}, 在线人数={}", userId, session.getId(), onlineNumber);
            return;
        }
        // ② 同用户重连时替换旧 session，否则推送还打到已断开的连接，前端一直「正在思考」
        if (old != this && old.session != null && old.session.isOpen() && old.session != session) {
            try {
                old.session.close();
            } catch (Exception e) {
                LOGGER.warn("关闭旧 AI WebSocket 失败 userId={}: {}", userId, e.getMessage());
            }
        }
        LOGGER.info("AI WebSocket 重连替换 session userId={}, session={}", userId, session.getId());
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
        if (!ToolUtil.isBlank(userId) && clients.containsKey(userId)) {
            onlineNumber--;
            clients.remove(userId);
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

        } catch (Exception e) {
            LOGGER.warn("发生了错误了: {}", e);
        }
    }

    /**
     * 发送给指定用户消息（同一 Session 串行发送，避免 TEXT_FULL_WRITING）
     *
     * @param message
     * @param userId
     */
    public void sendMessageTo(String message, String userId) {
        AiMessageWebSocket item = clients.get(userId);
        if (item == null || item.session == null || !item.session.isOpen()) {
            return;
        }
        // AsyncRemote 并发 sendText 会触发 TEXT_FULL_WRITING，必须按 session 串行
        synchronized (item.session) {
            try {
                if (item.session.isOpen()) {
                    item.session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                LOGGER.warn("AI WebSocket 推送失败 userId={}: {}", userId, e.getMessage());
            }
        }
    }

    /**
     * 获取当前在线的用户id
     *
     * @return
     */
    public static Set<String> getOnlineUserId() {
        return clients.keySet();
    }

    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }

    /**
     * 获取当前的session*
     *
     *
     */
     public synchronized Session getSession() {
         return this.session;
     }

}
