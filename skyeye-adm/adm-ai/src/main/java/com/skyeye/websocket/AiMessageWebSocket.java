/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * AI 消息 WebSocket。
 * <p>
 * 同一 userId 允许并存多条连接（Cloud / Auto 等），推送时广播。
 * 禁止「新连踢旧连」，否则多端会互相重连死循环。
 * <p>
 * 多实例场景通过 Redis Pub/Sub 跨节点分发，保证用户连在 A 节点、业务在 B 节点时仍能收到推送。
 */
@Component
@ServerEndpoint("/aiMessageWebSocket/{userId}")
public class AiMessageWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiMessageWebSocket.class);

    private static final Map<String, CopyOnWriteArraySet<AiMessageWebSocket>> clients = new ConcurrentHashMap<>();

    private static final int HIGH_WATER_MARK = 30;

    private static final long HIGH_WARN_INTERVAL_MS = 5 * 60 * 1000L;

    private static final AtomicLong LAST_HIGH_WARN_MS = new AtomicLong(0);

    /**
     * 跨节点分发频道。
     * JVM：-Dai.ws.cluster.channel=xxx
     * 环境变量：AI_WS_CLUSTER_CHANNEL=xxx
     */
    public static final String WS_CLUSTER_CHANNEL = resolveStringConfig(
        "ai.ws.cluster.channel", "AI_WS_CLUSTER_CHANNEL", "skyeye:adm:ws:ai:cluster:dispatch");

    /**
     * 当前节点 ID，用于忽略本节点自己发布的消息。
     * JVM：-Dai.ws.node.id=node-a
     * 环境变量：AI_WS_NODE_ID=node-a
     */
    private static final String NODE_ID = resolveNodeId();

    /**
     * @deprecated 请用 {@link #getOnlineCount()}
     */
    @Deprecated
    public static volatile int onlineNumber = 0;

    private Session session;

    private String userId;

    static {
        LOGGER.info("AI WebSocket 跨节点分发频道 -> {}, nodeId={}", WS_CLUSTER_CHANNEL, NODE_ID);
    }

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        this.userId = userId;
        this.session = session;
        CopyOnWriteArraySet<AiMessageWebSocket> set =
            clients.computeIfAbsent(userId, key -> new CopyOnWriteArraySet<>());
        set.add(this);
        syncOnlineNumber();
        LOGGER.info("AI WebSocket 接入 userId={}, session={}, userConn={}, userCount={}, totalConn={}, nodeId={}",
            userId, sessionId(session), set.size(), clients.size(), totalConnectionCount(), NODE_ID);
        maybeWarnHighWater("open");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.warn("AI WebSocket 错误 userId={}, session={}, msg={}",
            userId, sessionId(session), error == null ? null : error.getMessage());
    }

    @OnClose
    public void onClose() {
        if (ToolUtil.isBlank(userId)) {
            return;
        }
        CopyOnWriteArraySet<AiMessageWebSocket> set = clients.get(userId);
        boolean removed = false;
        if (set != null) {
            removed = set.remove(this);
            if (set.isEmpty()) {
                clients.remove(userId, set);
            }
        }
        syncOnlineNumber();
        if (removed) {
            LOGGER.info("AI WebSocket 关闭 userId={}, session={}, userCount={}, totalConn={}, nodeId={}",
                userId, sessionId(session), clients.size(), totalConnectionCount(), NODE_ID);
        } else {
            LOGGER.debug("AI WebSocket 关闭忽略(不在 map) userId={}, session={}, totalConn={}",
                userId, sessionId(session), totalConnectionCount());
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
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

    /**
     * 向指定用户推送：先本机，再通过 Redis 通知其他节点。
     */
    public void sendMessageTo(String message, String userId) {
        sendMessageToLocal(message, userId);
        publishClusterDispatch(message, userId);
    }

    /**
     * 仅向本机该用户的连接推送（供集群订阅回调使用，不再二次发布）。
     */
    public void sendMessageToLocal(String message, String userId) {
        CopyOnWriteArraySet<AiMessageWebSocket> set = clients.get(userId);
        if (set == null || set.isEmpty()) {
            LOGGER.debug("AI WebSocket 本机推送跳过(无可用连接) userId={}, nodeId={}", userId, NODE_ID);
            return;
        }
        for (AiMessageWebSocket item : set) {
            if (item == null || item.session == null || !item.session.isOpen()) {
                continue;
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
    }

    @SuppressWarnings("unchecked")
    private static void publishClusterDispatch(String message, String userId) {
        try {
            RedisTemplate<String, String> redisTemplate = SpringUtils.getBean("redisTemplate");
            Map<String, Object> payload = new HashMap<>();
            payload.put("fromNode", NODE_ID);
            payload.put("action", "USER");
            payload.put("message", message);
            payload.put("userId", userId);
            redisTemplate.convertAndSend(WS_CLUSTER_CHANNEL, JSONUtil.toJsonStr(payload));
        } catch (Exception e) {
            LOGGER.error("发布 AI WebSocket 跨节点消息失败. userId={}, error={}", userId, e.getMessage());
        }
    }

    /**
     * 处理 Redis 频道上的跨节点消息（由订阅器回调）。
     */
    public static void handleClusterDispatch(String payload) {
        try {
            if (ToolUtil.isBlank(payload)) {
                return;
            }
            String trimPayload = payload.trim();
            if (!(trimPayload.startsWith("{") && trimPayload.endsWith("}"))) {
                LOGGER.debug("忽略非 JSON 的 AI WebSocket 跨节点消息: {}", trimPayload);
                return;
            }
            JSONObject json = JSONUtil.parseObj(trimPayload);
            String fromNode = json.getStr("fromNode");
            if (ToolUtil.isBlank(fromNode) || NODE_ID.equals(fromNode)) {
                return;
            }
            String action = json.getStr("action");
            String message = json.getStr("message");
            String userId = json.getStr("userId");
            if (!"USER".equals(action) || ToolUtil.isBlank(message) || ToolUtil.isBlank(userId)) {
                return;
            }
            new AiMessageWebSocket().sendMessageToLocal(message, userId);
        } catch (Exception e) {
            LOGGER.error("处理 AI WebSocket 跨节点消息失败: {}", e.getMessage(), e);
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

    private static int totalConnectionCount() {
        int total = 0;
        for (CopyOnWriteArraySet<AiMessageWebSocket> set : clients.values()) {
            if (set != null) {
                total += set.size();
            }
        }
        return total;
    }

    private static void syncOnlineNumber() {
        onlineNumber = clients.size();
    }

    private static void maybeWarnHighWater(String reason) {
        int size = totalConnectionCount();
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
        LOGGER.warn("AI WebSocket 连接数偏高 totalConn={}, userCount={}, reason={}, nodeId={}, userIds样例={}",
            size, clients.size(), reason, NODE_ID, sampleUserIds(8));
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

    private static String resolveNodeId() {
        String configuredNodeId = System.getProperty("ai.ws.node.id");
        if (ToolUtil.isBlank(configuredNodeId)) {
            configuredNodeId = System.getenv("AI_WS_NODE_ID");
        }
        if (!ToolUtil.isBlank(configuredNodeId)) {
            return configuredNodeId;
        }
        String host = "unknown-host";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {
            // ignore
        }
        String pid = ManagementFactory.getRuntimeMXBean().getName();
        return host + "-" + pid;
    }

    private static String resolveStringConfig(String systemPropertyKey, String envKey, String defaultValue) {
        try {
            String value = System.getProperty(systemPropertyKey);
            if (ToolUtil.isBlank(value)) {
                value = System.getenv(envKey);
            }
            if (ToolUtil.isBlank(value)) {
                return defaultValue;
            }
            return value.trim();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
