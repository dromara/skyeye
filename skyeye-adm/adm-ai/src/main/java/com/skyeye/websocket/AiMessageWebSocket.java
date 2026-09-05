/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket;

import cn.hutool.core.util.StrUtil;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * AI 消息 WebSocket。
 * <p>
 * 同用户仅保留最新一条业务映射；关闭旧连接时不得误删新连接（见 onClose）。
 */
@Component
@ServerEndpoint("/aiMessageWebSocket/{userId}")
public class AiMessageWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiMessageWebSocket.class);

    /** 业务侧在线映射：userId -> 当前端点（同用户只保留最新） */
    private static final Map<String, AiMessageWebSocket> clients = new ConcurrentHashMap<>();

    /** 连接数偏高告警水位（按业务 mapSize，不是 Tomcat 真实会话数） */
    private static final int HIGH_WATER_MARK = 30;

    /** 偏高告警最短间隔，避免刷屏，便于事后 grep */
    private static final long HIGH_WARN_INTERVAL_MS = 5 * 60 * 1000L;

    private static final AtomicLong LAST_HIGH_WARN_MS = new AtomicLong(0);

    /**
     * @deprecated 请用 {@link #getOnlineCount()}；保留字段兼容旧调用，始终与 map 同步。
     */
    @Deprecated
    public static volatile int onlineNumber = 0;

    private Session session;

    private String userId;

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        this.userId = userId;
        this.session = session;
        AiMessageWebSocket old = clients.put(userId, this);
        syncOnlineNumber();
        if (old == null) {
            LOGGER.info("AI WebSocket 接入 userId={}, session={}, mapSize={}",
                userId, sessionId(session), clients.size());
            maybeWarnHighWater("open");
            return;
        }
        // 同用户重连：关掉旧 session；旧端点 onClose 只会 remove(userId, old)，不会误删当前 this
        if (old != this && old.session != null && old.session.isOpen() && old.session != session) {
            try {
                old.session.close();
            } catch (Exception e) {
                LOGGER.warn("关闭旧 AI WebSocket 失败 userId={}, oldSession={}: {}",
                    userId, sessionId(old.session), e.getMessage());
            }
        }
        LOGGER.info("AI WebSocket 重连替换 userId={}, newSession={}, oldSession={}, mapSize={}",
            userId, sessionId(session), sessionId(old.session), clients.size());
        maybeWarnHighWater("replace");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.warn("AI WebSocket 错误 userId={}, session={}, msg={}",
            userId, sessionId(session), error == null ? null : error.getMessage());
    }

    /**
     * 仅当 map 中仍是本实例时才移除，避免「关旧连误删新连」。
     */
    @OnClose
    public void onClose() {
        if (ToolUtil.isBlank(userId)) {
            return;
        }
        boolean removed = clients.remove(userId, this);
        syncOnlineNumber();
        if (removed) {
            LOGGER.info("AI WebSocket 关闭 userId={}, session={}, mapSize={}",
                userId, sessionId(session), clients.size());
        } else {
            // 已被新连接替换后的旧连接关闭：属正常路径，用 debug，需要时可临时开 debug 核对
            LOGGER.debug("AI WebSocket 旧连接关闭已忽略(map 已是新实例) userId={}, session={}, mapSize={}",
                userId, sessionId(session), clients.size());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            // 心跳很频繁，不要打 INFO，否则日志爆炸且干扰排查
            if (isPingMessage(message)) {
                LOGGER.debug("AI WebSocket ping userId={}, session={}", userId, sessionId(session));
                return;
            }
            LOGGER.info("AI WebSocket 消息 userId={}, session={}, body={}",
                userId, sessionId(session), abbreviate(message, 200));
            JSONUtil.toBean(message, JSONObject.class);
        } catch (Exception e) {
            LOGGER.warn("AI WebSocket 消息处理失败 userId={}, session={}: {}",
                userId, sessionId(session), e.getMessage());
        }
    }

    public void sendMessageTo(String message, String userId) {
        AiMessageWebSocket item = clients.get(userId);
        if (item == null || item.session == null || !item.session.isOpen()) {
            LOGGER.debug("AI WebSocket 推送跳过(无可用连接) userId={}", userId);
            return;
        }
        synchronized (item.session) {
            try {
                if (item.session.isOpen()) {
                    item.session.getBasicRemote().sendText(message);
                }
            } catch (Exception e) {
                LOGGER.warn("AI WebSocket 推送失败 userId={}, session={}: {}",
                    userId, sessionId(item.session), e.getMessage());
            }
        }
    }

    public static Set<String> getOnlineUserId() {
        return clients.keySet();
    }

    public static int getOnlineCount() {
        return clients.size();
    }

    public synchronized Session getSession() {
        return this.session;
    }

    private static void syncOnlineNumber() {
        onlineNumber = clients.size();
    }

    /**
     * 连接数偏高时打 WARN（5 分钟最多一次），事后 grep「AI WebSocket 连接数偏高」即可。
     */
    private static void maybeWarnHighWater(String reason) {
        int size = clients.size();
        if (size < HIGH_WATER_MARK) {
            return;
        }
        long now = System.currentTimeMillis();
        long prev = LAST_HIGH_WARN_MS.get();
        if (now - prev < HIGH_WARN_INTERVAL_MS) {
            return;
        }
        if (!LAST_HIGH_WARN_MS.compareAndSet(prev, now)) {
            return;
        }
        LOGGER.warn("AI WebSocket 连接数偏高 mapSize={}, reason={}, userIds样例={}, 请排查多标签/重连或会话泄漏",
            size, reason, sampleUserIds(8));
    }

    private static String sampleUserIds(int limit) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String id : clients.keySet()) {
            if (i >= limit) {
                sb.append("...");
                break;
            }
            if (i > 0) {
                sb.append(',');
            }
            sb.append(id);
            i++;
        }
        return sb.toString();
    }

    private static boolean isPingMessage(String message) {
        if (StrUtil.isBlank(message)) {
            return false;
        }
        String trim = message.trim();
        if ("ping".equalsIgnoreCase(trim)) {
            return true;
        }
        return trim.contains("\"type\"") && trim.contains("ping");
    }

    private static String abbreviate(String text, int max) {
        if (text == null) {
            return null;
        }
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max) + "...";
    }

    private static String sessionId(Session session) {
        return session == null ? "-" : session.getId();
    }
}
