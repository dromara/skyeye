/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.SocketConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.websocket.handler.client.TalkWebSocketClientMessageHandlerFactory;
import com.skyeye.websocket.service.WebSocketSendFailLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @ClassName: TalkWebSocket
 * @Description: 聊天/消息推送
 * @author: skyeye云系列--卫志强
 * @date: 2020年11月14日 下午9:36:38
 * @Copyright: 2020 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 * <p>
 * 多租户：连接地址可携带 query 参数 {@code tenantId}（与 HTTP 头一致），服务端按会话记录，
 * 发送失败落库时写入目标终端所属租户；未传时尝试 {@link TenantContext}（WebSocket 线程通常为 null）。
 */
@Component
@ServerEndpoint("/talkwebsocket/{userId}")
public class TalkWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(TalkWebSocket.class);

    /**
     * 在线人数（独立用户数）
     */
    private static final AtomicInteger onlineNumber = new AtomicInteger(0);

    /**
     * 总连接数（所有终端的连接数）
     */
    private static final AtomicInteger totalConnections = new AtomicInteger(0);

    /**
     * 以用户ID为key，该用户的所有WebSocket连接为值
     * 支持一个用户多终端同时在线
     */
    private static final Map<String, Set<TalkWebSocket>> userSessions = new ConcurrentHashMap<>();

    /**
     * 会话ID到用户ID的映射，用于快速定位会话属于哪个用户
     */
    private static final Map<String, String> sessionIdToUserId = new ConcurrentHashMap<>();

    /**
     * 会话与租户（握手 query：tenantId），用于发送失败日志等
     */
    private static final Map<String, String> sessionIdToTenantId = new ConcurrentHashMap<>();

    /**
     * 用户会话最后活跃时间，用于超时检测
     */
    private static final Map<String, Long> lastActiveTime = new ConcurrentHashMap<>();

    /**
     * 消息发送者，用于异步批量发送消息
     */
    private static final ExecutorService messageExecutor = new ThreadPoolExecutor(
        5, 10, 60, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(2000),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 心跳超时时间（毫秒），默认60秒
     */
    private static final long HEARTBEAT_TIMEOUT = 60000;

    /**
     * 会话超时时间（毫秒），默认30分钟
     */
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;

    /**
     * 单用户最大连接数，防止异常客户端占用过多连接（<=0表示不限制）
     * <p>
     * 配置优先级：JVM -D > 环境变量 > 默认值
     * JVM参数：-Dws.max.connections.per.user=20
     * 环境变量：WS_MAX_CONNECTIONS_PER_USER=20
     */
    private static final int MAX_CONNECTIONS_PER_USER = resolveIntConfig("ws.max.connections.per.user", "WS_MAX_CONNECTIONS_PER_USER", 5);

    /**
     * 系统最大连接数（<=0表示不限制，默认不限制）
     * <p>
     * 配置优先级：JVM -D > 环境变量 > 默认值
     * JVM参数：-Dws.max.total.connections=0
     * 环境变量：WS_MAX_TOTAL_CONNECTIONS=0
     */
    private static final int MAX_TOTAL_CONNECTIONS = resolveIntConfig("ws.max.total.connections", "WS_MAX_TOTAL_CONNECTIONS", 0);

    /**
     * 是否在超过max阈值时拒绝连接（false=只告警不拒绝）
     * <p>
     * JVM参数：-Dws.reject.on.limit=false
     * 环境变量：WS_REJECT_ON_LIMIT=false
     */
    private static final boolean REJECT_ON_LIMIT = resolveBooleanConfig("ws.reject.on.limit", "WS_REJECT_ON_LIMIT", false);

    /**
     * 系统连接数告警阈值（<=0表示关闭告警）
     * <p>
     * JVM参数：-Dws.warn.total.connections=200000
     * 环境变量：WS_WARN_TOTAL_CONNECTIONS=200000
     */
    private static final int WARN_TOTAL_CONNECTIONS = resolveIntConfig("ws.warn.total.connections", "WS_WARN_TOTAL_CONNECTIONS", 0);

    /**
     * 单用户连接数告警阈值（<=0表示关闭告警）
     * <p>
     * JVM参数：-Dws.warn.connections.per.user=10
     * 环境变量：WS_WARN_CONNECTIONS_PER_USER=10
     */
    private static final int WARN_CONNECTIONS_PER_USER = resolveIntConfig("ws.warn.connections.per.user", "WS_WARN_CONNECTIONS_PER_USER", 0);

    /**
     * 连接/发送可观测性指标
     */
    private static final AtomicLong rejectedConnectionCount = new AtomicLong(0);
    private static final AtomicLong broadcastRejectCount = new AtomicLong(0);
    private static final AtomicLong sendFailureCount = new AtomicLong(0);
    /**
     * 跨节点WebSocket消息分发频道
     * <p>
     * JVM参数：-Dws.cluster.channel=xxx
     * 环境变量：WS_CLUSTER_CHANNEL=xxx
     */
    public static final String WS_CLUSTER_CHANNEL = resolveStringConfig(
        "ws.cluster.channel", "WS_CLUSTER_CHANNEL", "skyeye:userauth:ws:talk:cluster:dispatch");
    private static final String WS_ONLINE_USERS_KEY = "ws:talk:online:users";
    private static final String WS_USER_CONN_PREFIX = "ws:talk:user:conn:";

    /**
     * 当前节点ID（用于避免消费自己发布的消息）
     * <p>
     * JVM参数：-Dws.node.id=node-a
     * 环境变量：WS_NODE_ID=node-a
     */
    private static final String NODE_ID = resolveNodeId();

    /**
     * 会话
     */
    private Session session;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户会话ID，用于标识具体会话
     */
    private String sessionId;

    /**
     * 当前连接所属租户（握手参数 tenantId）
     */
    private String tenantId;

    /**
     * 启动定时任务，处理超时连接
     */
    static {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 定时检查心跳超时的连接
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkHeartbeatTimeout();
            } catch (Exception e) {
                LOGGER.error("检查心跳超时异常", e);
            }
        }, 30, 30, TimeUnit.SECONDS);

        // 定时检查会话超时
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkSessionTimeout();
            } catch (Exception e) {
                LOGGER.error("检查会话超时异常", e);
            }
        }, 10, 10, TimeUnit.MINUTES);

        // 定时打印连接与发送指标，方便容量观察
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logRuntimeMetrics();
            } catch (Exception e) {
                LOGGER.error("打印WebSocket运行指标异常", e);
            }
        }, 1, 1, TimeUnit.MINUTES);

        LOGGER.info(
            "WebSocket连接阈值配置 -> rejectOnLimit: {}, maxTotal: {}, maxPerUser: {}, warnTotal: {}, warnPerUser: {}",
            REJECT_ON_LIMIT, MAX_TOTAL_CONNECTIONS, MAX_CONNECTIONS_PER_USER, WARN_TOTAL_CONNECTIONS, WARN_CONNECTIONS_PER_USER
        );
        LOGGER.info("WebSocket跨节点分发频道 -> {}", WS_CLUSTER_CHANNEL);

        // 应用关闭时清理资源
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closeAllConnections();
                messageExecutor.shutdown();
                scheduler.shutdown();
            } catch (Exception e) {
                LOGGER.error("关闭连接资源异常", e);
            }
        }));
    }

    /**
     * 建立连接
     *
     * @param session WebSocket会话
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        try {
            if (ToolUtil.isBlank(userId)) {
                session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, "userId不能为空"));
                return;
            }

            int currentTotal = readNonNegative(totalConnections);
            if (WARN_TOTAL_CONNECTIONS > 0 && currentTotal >= WARN_TOTAL_CONNECTIONS) {
                LOGGER.warn("系统连接数达到告警阈值: {}, 当前: {}, userId: {}", WARN_TOTAL_CONNECTIONS, currentTotal, userId);
            }
            if (MAX_TOTAL_CONNECTIONS > 0 && currentTotal >= MAX_TOTAL_CONNECTIONS) {
                if (REJECT_ON_LIMIT) {
                    rejectedConnectionCount.incrementAndGet();
                    session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "连接已达上限"));
                    LOGGER.warn("拒绝连接，达到系统连接上限: {}, userId: {}", MAX_TOTAL_CONNECTIONS, userId);
                    return;
                } else {
                    LOGGER.warn("系统连接数超过max阈值，但当前为告警模式未拒绝。max: {}, current: {}, userId: {}",
                        MAX_TOTAL_CONNECTIONS, currentTotal, userId);
                }
            }

            this.userId = userId;
            this.session = session;
            this.sessionId = session.getId();

            Set<TalkWebSocket> userSocketSet = userSessions.computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet());
            boolean isNewUser;
            synchronized (userSocketSet) {
                int userCurrentConnections = userSocketSet.size();
                if (WARN_CONNECTIONS_PER_USER > 0 && userCurrentConnections >= WARN_CONNECTIONS_PER_USER) {
                    LOGGER.warn("用户连接数达到告警阈值: userId: {}, warn: {}, current: {}", userId, WARN_CONNECTIONS_PER_USER, userCurrentConnections);
                }
                if (MAX_CONNECTIONS_PER_USER > 0 && userCurrentConnections >= MAX_CONNECTIONS_PER_USER) {
                    if (REJECT_ON_LIMIT) {
                        rejectedConnectionCount.incrementAndGet();
                        session.close(new CloseReason(CloseReason.CloseCodes.TRY_AGAIN_LATER, "用户连接数超限"));
                        LOGGER.warn("拒绝连接，用户 {} 超过最大连接数: {}", userId, MAX_CONNECTIONS_PER_USER);
                        return;
                    } else {
                        LOGGER.warn("用户连接数超过max阈值，但当前为告警模式未拒绝。userId: {}, max: {}, current: {}",
                            userId, MAX_CONNECTIONS_PER_USER, userCurrentConnections);
                    }
                }
                isNewUser = userSocketSet.isEmpty();
                userSocketSet.add(this);
            }

            // 设置会话配置
            this.session.setMaxIdleTimeout(120000); // 设置会话超时时间为2分钟

            String resolvedTenant = resolveTenantIdForHandshake(session);
            this.tenantId = ToolUtil.isBlank(resolvedTenant) ? null : resolvedTenant;
            sessionIdToTenantId.put(sessionId, this.tenantId == null ? "" : this.tenantId);

            // 更新会话ID到用户ID的映射
            sessionIdToUserId.put(sessionId, userId);

            // 更新最后活跃时间
            lastActiveTime.put(sessionId, System.currentTimeMillis());
            totalConnections.incrementAndGet();
            if (isNewUser) {
                onlineNumber.incrementAndGet();
            }
            markUserOnline(userId);
            LOGGER.info("新连接加入 - 客户端ID: {}, 用户ID: {}, 租户: {}, 当前在线人数: {}, 总连接数: {}",
                session.getId(), userId, this.tenantId != null ? this.tenantId : "-", onlineNumber.get(), totalConnections.get());

            // 如果是新用户，通知其他用户我上线了
            if (isNewUser) {
                Map<String, Object> map1 = new HashMap<>();
                map1.put("messageType", SocketConstants.MessageType.First.getType());
                map1.put("userId", userId);
                sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId); // 不给自己发送
            }

            // 给自己的所有终端发送一条消息：告诉当前有谁在线
            Map<String, Object> map2 = new HashMap<>();
            map2.put("messageType", SocketConstants.MessageType.Third.getType());
            map2.put("onlineUsers", getOnlineUserId());
            map2.put("terminals", userSessions.get(userId).size());  // 当前用户的终端数
            sendMessageTo(JSONUtil.toJsonStr(map2), userId, null);
        } catch (Exception e) {
            LOGGER.error("建立连接异常: {}", e.getMessage(), e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.warn("服务端发生了错误: {}", error.getMessage());
        try {
            // 通知客户端出错
            if (session.isOpen()) {
                Map<String, Object> errorMsg = new HashMap<>();
                errorMsg.put("messageType", "error");
                errorMsg.put("message", "连接发生错误，请尝试重新连接");
                session.getAsyncRemote().sendText(JSONUtil.toJsonStr(errorMsg));
            }
        } catch (Exception e) {
            LOGGER.error("发送错误通知失败: {}", e.getMessage());
        }
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose() {
        if (ToolUtil.isBlank(userId) || sessionId == null) {
            return;
        }

        try {
            // 更新会话ID到用户ID的映射
            sessionIdToUserId.remove(sessionId);
            sessionIdToTenantId.remove(sessionId);
            lastActiveTime.remove(sessionId);

            // 从用户的会话集合中移除当前会话
            Set<TalkWebSocket> userSocketSet = userSessions.get(userId);
            if (userSocketSet != null) {
                boolean removed = userSocketSet.remove(this);
                if (removed) {
                    safeDecrement(totalConnections);
                }

                // 如果用户的所有连接都断开了，则从用户列表中移除
                if (removed && userSocketSet.isEmpty() && userSessions.remove(userId, userSocketSet)) {
                    safeDecrement(onlineNumber);
                    markUserOffline(userId);

                    // 通知其他用户我下线了
                    Map<String, Object> map1 = new HashMap<>();
                    map1.put("messageType", SocketConstants.MessageType.Second.getType());
                    map1.put("onlineUsers", getOnlineUserId());
                    map1.put("userId", userId);
                    sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);

                    LOGGER.info("用户完全离线 - 用户ID: {}, 当前在线人数: {}, 总连接数: {}",
                        userId, onlineNumber.get(), totalConnections.get());
                } else {
                    LOGGER.info("终端断开连接 - 用户ID: {}, 剩余终端数: {}, 当前在线人数: {}, 总连接数: {}",
                        userId, userSocketSet.size(), onlineNumber.get(), totalConnections.get());
                }
            }
        } catch (Exception e) {
            LOGGER.error("关闭连接异常: {}", e.getMessage(), e);
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

            // 更新最后活跃时间
            if (sessionId != null) {
                lastActiveTime.put(sessionId, System.currentTimeMillis());
            }

            JSONObject jsonObject = JSONUtil.toBean(message, null);

            // 处理心跳消息
            String typeStr = jsonObject.getStr("type");
            if ("ping".equals(typeStr)) {
                // 回复pong消息
                JSONObject pongMessage = new JSONObject();
                pongMessage.put("type", "pong");
                pongMessage.put("timestamp", System.currentTimeMillis());
                if (this.session.isOpen()) {
                    this.session.getAsyncRemote().sendText(JSONUtil.toJsonStr(pongMessage));
                }
                return;
            }

            // 处理其他类型消息（按 type 分发到策略处理器）
            int type = Integer.parseInt(typeStr);
            TalkWebSocketClientMessageHandlerFactory.dispatch(type, jsonObject, this);
        } catch (Exception e) {
            LOGGER.warn("处理消息时发生错误: {}", e.getMessage(), e);
            try {
                // 通知客户端消息处理错误
                JSONObject errorMsg = new JSONObject();
                errorMsg.put("type", "error");
                errorMsg.put("message", "消息处理失败");
                session.getAsyncRemote().sendText(JSONUtil.toJsonStr(errorMsg));
            } catch (Exception ex) {
                LOGGER.error("发送错误通知失败: {}", ex.getMessage());
            }
        }
    }

    /**
     * 发送给自己的其他终端
     *
     * @param messageStr 消息内容
     * @param talkId     聊天对象id(接收者id)
     * @param msgType    消息类型
     */
    public void sendMessageToMe(String messageStr, String talkId, Integer msgType) {
        Map<String, Object> sendMsg = new HashMap<>();
        // 我的用户id
        sendMsg.put("myId", this.userId);
        sendMsg.put("type", SocketConstants.MessageType.SendToMe.getType());
        sendMsg.put("message", messageStr);
        sendMsg.put("msgType", msgType);
        sendMsg.put("talkId", talkId);
        sendMessageTo(JSONUtil.toJsonStr(sendMsg), this.userId, this.sessionId);
    }

    /**
     * 供消息处理器获取当前连接用户 id。
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 供消息处理器获取当前连接会话。
     */
    public Session getWsSession() {
        return session;
    }

    /**
     * 发送消息给指定会话
     *
     * @param message 消息内容
     * @param session 会话对象
     */
    public void sendMessageToSession(String message, Session session) {
        if (session != null && session.isOpen()) {
            sendTextWithRetry(message, session, session.getId(), "session");
        }
    }

    /**
     * 发送消息给指定用户的所有终端
     *
     * @param message      消息内容
     * @param userId       用户ID
     * @param notSessionId 不发送的终端id
     */
    public void sendMessageTo(String message, String userId, String notSessionId) {
        sendMessageToLocal(message, userId, notSessionId);
        publishClusterDispatch("USER", message, userId, null, notSessionId);
    }

    /**
     * 仅向本机指定用户终端发送（不进行跨节点转发）
     */
    private void sendMessageToLocal(String message, String userId, String notSessionId) {
        Set<TalkWebSocket> sockets = userSessions.get(userId);
        if (sockets != null && !sockets.isEmpty()) {
            for (TalkWebSocket socket : sockets) {
                try {
                    if (socket.session.isOpen()) {
                        if (notSessionId != null && notSessionId.equals(socket.sessionId)) {
                            continue;
                        }
                        sendTextWithRetry(message, socket.session, socket.sessionId, userId);
                    }
                } catch (Exception e) {
                    LOGGER.error("发送消息给用户 {} 的终端 {} 失败: {}",
                        userId, socket.sessionId, e.getMessage());
                }
            }
        }
    }

    /**
     * 发送给全部用户的所有终端，除了指定用户
     *
     * @param message       消息内容
     * @param excludeUserId 排除的用户ID（不发送给该用户的所有终端）
     */
    public void sendMessageToAllExcept(String message, String excludeUserId) {
        sendMessageToAllExceptLocal(message, excludeUserId);
        publishClusterDispatch("ALL_EXCEPT", message, null, excludeUserId, null);
    }

    /**
     * 仅向本机所有用户发送，排除指定用户（不进行跨节点转发）
     */
    private void sendMessageToAllExceptLocal(String message, String excludeUserId) {
        // 使用线程池异步发送消息
        try {
            messageExecutor.submit(() -> {
                for (Map.Entry<String, Set<TalkWebSocket>> entry : userSessions.entrySet()) {
                    String userId = entry.getKey();
                    // 排除指定用户
                    if (excludeUserId != null && excludeUserId.equals(userId)) {
                        continue;
                    }

                    Set<TalkWebSocket> sockets = entry.getValue();
                    for (TalkWebSocket socket : sockets) {
                        try {
                            if (socket.session.isOpen()) {
                                sendTextWithRetry(message, socket.session, socket.sessionId, userId);
                            }
                        } catch (Exception e) {
                            LOGGER.error("发送全体消息给用户 {} 的终端 {} 失败: {}",
                                userId, socket.sessionId, e.getMessage());
                        }
                    }
                }
            });
        } catch (RejectedExecutionException ex) {
            broadcastRejectCount.incrementAndGet();
            LOGGER.warn("广播任务被拒绝，当前系统繁忙。excludeUserId: {}", excludeUserId);
        }
    }

    /**
     * 发送给全部用户的所有终端，不排除任何用户
     *
     * @param message 消息内容
     */
    public void sendMessageToAll(String message) {
        sendMessageToAllExceptLocal(message, null);
        publishClusterDispatch("ALL", message, null, null, null);
    }

    /**
     * 检查心跳超时的连接
     */
    private static void checkHeartbeatTimeout() {
        long now = System.currentTimeMillis();

        // 遍历所有用户的所有连接
        for (Map.Entry<String, Set<TalkWebSocket>> entry : userSessions.entrySet()) {
            String userId = entry.getKey();
            Set<TalkWebSocket> sockets = entry.getValue();
            Set<TalkWebSocket> timeoutSockets = new HashSet<>();
            for (TalkWebSocket socket : sockets) {
                Long lastActive = lastActiveTime.get(socket.sessionId);
                if (lastActive != null && now - lastActive > HEARTBEAT_TIMEOUT) {
                    timeoutSockets.add(socket);
                }
            }
            if (timeoutSockets.isEmpty()) {
                continue;
            }

            LOGGER.info("用户 {} 检测到 {} 个心跳超时连接", userId, timeoutSockets.size());
            for (TalkWebSocket socket : timeoutSockets) {
                try {
                    if (socket.session.isOpen()) {
                        socket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "心跳超时"));
                    }
                } catch (IOException e) {
                    LOGGER.error("关闭超时连接失败: {}", e.getMessage());
                } finally {
                    sessionIdToUserId.remove(socket.sessionId);
                    sessionIdToTenantId.remove(socket.sessionId);
                    lastActiveTime.remove(socket.sessionId);
                    if (sockets.remove(socket)) {
                        safeDecrement(totalConnections);
                    }
                }
            }

            if (sockets.isEmpty() && userSessions.remove(userId, sockets)) {
                safeDecrement(onlineNumber);
                markUserOffline(userId);
                Map<String, Object> map1 = new HashMap<>();
                map1.put("messageType", SocketConstants.MessageType.Second.getType());
                map1.put("onlineUsers", getOnlineUserId());
                map1.put("userId", userId);
                TalkWebSocket tempSocket = new TalkWebSocket();
                tempSocket.sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);
            }
        }
    }

    /**
     * 检查会话超时，清理长时间无活动的会话
     */
    private static void checkSessionTimeout() {
        long now = System.currentTimeMillis();
        List<String> timeoutSessions = lastActiveTime.entrySet().stream()
            .filter(entry -> now - entry.getValue() > SESSION_TIMEOUT)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());

        for (String timeoutSessionId : timeoutSessions) {
            String userId = sessionIdToUserId.remove(timeoutSessionId);
            sessionIdToTenantId.remove(timeoutSessionId);
            lastActiveTime.remove(timeoutSessionId);
            if (ToolUtil.isBlank(userId)) {
                continue;
            }
            Set<TalkWebSocket> sockets = userSessions.get(userId);
            if (sockets == null || sockets.isEmpty()) {
                continue;
            }

            TalkWebSocket timeoutSocket = null;
            for (TalkWebSocket socket : sockets) {
                if (timeoutSessionId.equals(socket.sessionId)) {
                    timeoutSocket = socket;
                    break;
                }
            }
            if (timeoutSocket == null) {
                continue;
            }
            try {
                if (timeoutSocket.session.isOpen()) {
                    timeoutSocket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "会话超时"));
                }
            } catch (IOException e) {
                LOGGER.error("关闭超时会话失败: {}", e.getMessage());
            } finally {
                if (sockets.remove(timeoutSocket)) {
                    safeDecrement(totalConnections);
                }
            }

            if (sockets.isEmpty() && userSessions.remove(userId, sockets)) {
                safeDecrement(onlineNumber);
                markUserOffline(userId);
                Map<String, Object> map1 = new HashMap<>();
                map1.put("messageType", SocketConstants.MessageType.Second.getType());
                map1.put("onlineUsers", getOnlineUserId());
                map1.put("userId", userId);
                TalkWebSocket tempSocket = new TalkWebSocket();
                tempSocket.sendMessageToAllExcept(JSONUtil.toJsonStr(map1), userId);

                LOGGER.info("用户 {} 会话全部超时离线，当前在线人数: {}, 总连接数: {}",
                    userId, onlineNumber.get(), totalConnections.get());
            }
        }
    }

    /**
     * 关闭所有连接
     */
    private static void closeAllConnections() {
        for (Map.Entry<String, Set<TalkWebSocket>> entry : userSessions.entrySet()) {
            for (TalkWebSocket socket : entry.getValue()) {
                try {
                    if (socket.session.isOpen()) {
                        socket.session.close(new CloseReason(CloseReason.CloseCodes.GOING_AWAY, "服务器关闭"));
                    }
                } catch (IOException e) {
                    LOGGER.error("关闭连接失败: {}", e.getMessage());
                }
            }
        }
        userSessions.clear();
        sessionIdToUserId.clear();
        sessionIdToTenantId.clear();
        lastActiveTime.clear();
        onlineNumber.set(0);
        totalConnections.set(0);
    }

    /**
     * 检查用户是否在线
     *
     * @param userId 用户ID
     * @return 是否在线
     */
    public static boolean isUserOnline(String userId) {
        Set<TalkWebSocket> sockets = userSessions.get(userId);
        if (sockets == null || sockets.isEmpty()) {
            return false;
        }

        // 检查是否至少有一个连接是打开的
        for (TalkWebSocket socket : sockets) {
            if (socket.session.isOpen()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取当前在线的用户id
     *
     * @return 在线用户ID集合
     */
    public static Set<String> getOnlineUserId() {
        try {
            JedisClientService jedisClientService = SpringUtils.getBean(JedisClientService.class);
            return jedisClientService.smembers(WS_ONLINE_USERS_KEY);
        } catch (Exception e) {
            LOGGER.warn("获取Redis在线用户失败，使用本机在线用户兜底: {}", e.getMessage());
            return userSessions.keySet();
        }
    }

    /**
     * 获取在线人数（独立用户数）
     *
     * @return 在线人数
     */
    public static synchronized int getOnlineCount() {
        return readNonNegative(onlineNumber);
    }

    /**
     * 获取总连接数（所有终端的连接数）
     *
     * @return 总连接数
     */
    public static synchronized int getTotalConnections() {
        return readNonNegative(totalConnections);
    }

    /**
     * 获取指定用户的连接数
     *
     * @param userId 用户ID
     * @return 该用户的连接数
     */
    public static int getUserConnectionCount(String userId) {
        Set<TalkWebSocket> sockets = userSessions.get(userId);
        return sockets != null ? sockets.size() : 0;
    }

    public static long getRejectedConnectionCount() {
        return rejectedConnectionCount.get();
    }

    public static long getBroadcastRejectCount() {
        return broadcastRejectCount.get();
    }

    public static long getSendFailureCount() {
        return sendFailureCount.get();
    }

    public static int getActiveSessionRecordCount() {
        return lastActiveTime.size();
    }

    public static Map<String, Object> getRuntimeMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("onlineCount", getOnlineCount());
        metrics.put("clusterOnlineCount", getOnlineUserId().size());
        metrics.put("totalConnections", getTotalConnections());
        metrics.put("activeSessionRecords", getActiveSessionRecordCount());
        metrics.put("rejectedConnectionCount", getRejectedConnectionCount());
        metrics.put("broadcastRejectCount", getBroadcastRejectCount());
        metrics.put("sendFailureCount", getSendFailureCount());
        metrics.put("collectTime", DateUtil.getTimeAndToString());
        return metrics;
    }

    /**
     * 从握手 query（tenantId）或当前线程租户上下文解析租户
     */
    private static String resolveTenantIdForHandshake(Session wsSession) {
        try {
            if (wsSession != null) {
                Map<String, List<String>> pm = wsSession.getRequestParameterMap();
                if (pm != null) {
                    List<String> list = pm.get("tenantId");
                    if (list != null && !list.isEmpty()) {
                        String v = list.get(0);
                        if (StrUtil.isNotEmpty(v)) {
                            return v.trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("解析WebSocket租户参数失败: {}", e.getMessage());
        }
        String ctx = TenantContext.getTenantId();
        return ToolUtil.isBlank(ctx) ? null : ctx.trim();
    }

    private static void markUserOnline(String userId) {
        try {
            JedisClientService jedisClientService = SpringUtils.getBean(JedisClientService.class);
            Long count = jedisClientService.incrByData(WS_USER_CONN_PREFIX + userId, 1);
            if (count != null && count > 0) {
                jedisClientService.sadd(WS_ONLINE_USERS_KEY, userId);
            }
        } catch (Exception e) {
            LOGGER.warn("标记用户上线失败, userId: {}, error: {}", userId, e.getMessage());
        }
    }

    private static void markUserOffline(String userId) {
        try {
            JedisClientService jedisClientService = SpringUtils.getBean(JedisClientService.class);
            Long count = jedisClientService.incrByData(WS_USER_CONN_PREFIX + userId, -1);
            if (count == null || count <= 0) {
                jedisClientService.del(WS_USER_CONN_PREFIX + userId);
                jedisClientService.srem(WS_ONLINE_USERS_KEY, userId);
            }
        } catch (Exception e) {
            LOGGER.warn("标记用户下线失败, userId: {}, error: {}", userId, e.getMessage());
        }
    }

    private static void sendTextWithRetry(String message, Session session, String sessionId, String target) {
        try {
            session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            LOGGER.warn("发送消息失败，准备重试。target: {}, sessionId: {}, error: {}", target, sessionId, e.getMessage());
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception retryError) {
                sendFailureCount.incrementAndGet();
                LOGGER.error("发送消息重试失败。target: {}, sessionId: {}, error: {}", target, sessionId, retryError.getMessage());
                String failTenant = sessionIdToTenantId.getOrDefault(sessionId, "");
                recordSendFailure(target, sessionId, "basic", message, retryError.getMessage(), failTenant);
            }
        }
    }

    private static void logRuntimeMetrics() {
        LOGGER.info(
            "WebSocket运行指标 -> 在线用户: {}, 总连接: {}, 拒绝连接累计: {}, 广播拒绝累计: {}, 发送失败累计: {}, 活跃session记录: {}",
            getOnlineCount(), getTotalConnections(), rejectedConnectionCount.get(),
            broadcastRejectCount.get(), sendFailureCount.get(), lastActiveTime.size()
        );
    }

    private static int readNonNegative(AtomicInteger counter) {
        return Math.max(counter.get(), 0);
    }

    private static void safeDecrement(AtomicInteger counter) {
        counter.updateAndGet(value -> Math.max(value - 1, 0));
    }

    private static void recordSendFailure(String targetUserId, String sessionId, String sendStage, String message, String errorMessage, String tenantId) {
        try {
            WebSocketSendFailLogService failLogService = SpringUtils.getBean(WebSocketSendFailLogService.class);
            failLogService.saveSendFailLog(
                targetUserId,
                sessionId,
                sendStage,
                truncate(message, 2000),
                truncate(errorMessage, 1000),
                NODE_ID,
                tenantId
            );
        } catch (Exception e) {
            LOGGER.warn("记录WebSocket发送失败日志异常: {}", e.getMessage());
        }
    }

    private static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }

    /**
     * 发布跨节点分发消息
     */
    @SuppressWarnings("unchecked")
    private static void publishClusterDispatch(String action, String message, String userId, String excludeUserId, String notSessionId) {
        try {
            RedisTemplate<String, String> redisTemplate = SpringUtils.getBean("redisTemplate");
            Map<String, Object> payload = new HashMap<>();
            payload.put("fromNode", NODE_ID);
            payload.put("action", action);
            payload.put("message", message);
            payload.put("userId", userId);
            payload.put("excludeUserId", excludeUserId);
            payload.put("notSessionId", notSessionId);
            redisTemplate.convertAndSend(WS_CLUSTER_CHANNEL, JSONUtil.toJsonStr(payload));
        } catch (Exception e) {
            LOGGER.error("发布跨节点WebSocket消息失败. action: {}, userId: {}, error: {}", action, userId, e.getMessage());
        }
    }

    /**
     * 处理来自Redis频道的跨节点消息（由订阅器回调）
     */
    public static void handleClusterDispatch(String payload) {
        try {
            if (ToolUtil.isBlank(payload)) {
                return;
            }
            String trimPayload = payload.trim();
            // Redis 同频道可能存在其他业务消息，非 JSON 直接忽略，避免解析异常刷屏。
            if (!(trimPayload.startsWith("{") && trimPayload.endsWith("}"))) {
                LOGGER.debug("忽略非JSON跨节点消息: {}", trimPayload);
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
            String excludeUserId = json.getStr("excludeUserId");
            String notSessionId = json.getStr("notSessionId");
            if (ToolUtil.isBlank(action) || ToolUtil.isBlank(message)) {
                return;
            }
            TalkWebSocket dispatcher = new TalkWebSocket();
            if ("USER".equals(action)) {
                dispatcher.sendMessageToLocal(message, userId, notSessionId);
            } else if ("ALL_EXCEPT".equals(action)) {
                dispatcher.sendMessageToAllExceptLocal(message, excludeUserId);
            } else if ("ALL".equals(action)) {
                dispatcher.sendMessageToAllExceptLocal(message, null);
            }
        } catch (Exception e) {
            LOGGER.error("处理跨节点WebSocket消息失败: {}", e.getMessage(), e);
        }
    }

    private static String resolveNodeId() {
        String configuredNodeId = System.getProperty("ws.node.id");
        if (ToolUtil.isBlank(configuredNodeId)) {
            configuredNodeId = System.getenv("WS_NODE_ID");
        }
        if (!ToolUtil.isBlank(configuredNodeId)) {
            return configuredNodeId;
        }
        String host = "unknown-host";
        try {
            host = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception ignored) {
            // ignore
        }
        String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return host + "-" + pid;
    }

    private static int resolveIntConfig(String systemPropertyKey, String envKey, int defaultValue) {
        try {
            String value = System.getProperty(systemPropertyKey);
            if (ToolUtil.isBlank(value)) {
                value = System.getenv(envKey);
            }
            if (ToolUtil.isBlank(value)) {
                return defaultValue;
            }
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
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

    private static boolean resolveBooleanConfig(String systemPropertyKey, String envKey, boolean defaultValue) {
        try {
            String value = System.getProperty(systemPropertyKey);
            if (ToolUtil.isBlank(value)) {
                value = System.getenv(envKey);
            }
            if (ToolUtil.isBlank(value)) {
                return defaultValue;
            }
            return Boolean.parseBoolean(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
