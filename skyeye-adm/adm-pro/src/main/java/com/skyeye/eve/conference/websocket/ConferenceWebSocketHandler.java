package com.skyeye.eve.conference.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ConferenceWebSocketHandler extends TextWebSocketHandler {

    // 存储会议室的所有连接，key为会议号，value为该会议的所有参会者session
    private static final Map<String, Map<String, WebSocketSession>> conferenceRooms = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String conferenceNo = getConferenceNo(session);
        String userId = getUserId(session);

        // WebRTC 信令 / 聊天体积远小于该上限；与全局容器缓冲保持一致
        session.setTextMessageSizeLimit(256 * 1024);
        session.setBinaryMessageSizeLimit(256 * 1024);

        // 将用户加入会议室
        conferenceRooms.computeIfAbsent(conferenceNo, k -> new ConcurrentHashMap<>())
            .put(userId, session);

        // 广播新用户加入消息
        broadcastUserJoin(conferenceNo, userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String conferenceNo = getConferenceNo(session);
        String userId = getUserId(session);

        JSONObject msgData = JSON.parseObject(message.getPayload());
        String type = msgData.getString("type");

        switch (type) {
            case "offer":
            case "answer":
            case "ice-candidate":
                // WebRTC信令转发
                handleWebRTCSignal(conferenceNo, userId, msgData);
                break;
            case "chat":
                // 聊天消息广播
                broadcastChatMessage(conferenceNo, userId, msgData);
                break;
            case "screen-share":
                // 屏幕共享信令
                handleScreenShare(conferenceNo, userId, msgData);
                break;
            case "recording":
                // 录制控制信令
                handleRecording(conferenceNo, userId, msgData);
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String conferenceNo = getConferenceNo(session);
        String userId = getUserId(session);

        // 将用户从会议室移除
        Map<String, WebSocketSession> room = conferenceRooms.get(conferenceNo);
        if (room != null) {
            room.remove(userId);
            if (room.isEmpty()) {
                conferenceRooms.remove(conferenceNo);
            }
        }

        // 广播用户离开消息
        broadcastUserLeave(conferenceNo, userId);
    }

    private void handleWebRTCSignal(String conferenceNo, String fromUserId, JSONObject signal) {
        String toUserId = signal.getString("to");
        Map<String, WebSocketSession> room = conferenceRooms.get(conferenceNo);

        if (room != null && room.containsKey(toUserId)) {
            WebSocketSession toSession = room.get(toUserId);
            try {
                signal.put("from", fromUserId);
                toSession.sendMessage(new TextMessage(signal.toJSONString()));
            } catch (Exception e) {
                log.error("发送WebRTC信令失败", e);
            }
        }
    }

    private void broadcastChatMessage(String conferenceNo, String fromUserId, JSONObject msgData) {
        Map<String, WebSocketSession> room = conferenceRooms.get(conferenceNo);
        if (room != null) {
            JSONObject broadcastMsg = new JSONObject();
            broadcastMsg.put("type", "chat");
            broadcastMsg.put("from", fromUserId);
            broadcastMsg.put("content", msgData.getString("content"));

            room.forEach((userId, session) -> {
                if (!userId.equals(fromUserId)) {
                    try {
                        session.sendMessage(new TextMessage(broadcastMsg.toJSONString()));
                    } catch (Exception e) {
                        log.error("发送聊天消息失败", e);
                    }
                }
            });
        }
    }

    private void handleScreenShare(String conferenceNo, String userId, JSONObject signal) {
        // 处理屏幕共享信令，类似WebRTC信令处理
        Map<String, WebSocketSession> room = conferenceRooms.get(conferenceNo);
        if (room != null) {
            room.forEach((targetUserId, session) -> {
                if (!targetUserId.equals(userId)) {
                    try {
                        signal.put("from", userId);
                        session.sendMessage(new TextMessage(signal.toJSONString()));
                    } catch (Exception e) {
                        log.error("发送屏幕共享信令失败", e);
                    }
                }
            });
        }
    }

    private void handleRecording(String conferenceNo, String userId, JSONObject signal) {
        // 处理录制控制信令
        String action = signal.getString("action"); // start/stop
        if ("start".equals(action)) {
            // 开始录制逻辑
            startRecording(conferenceNo);
        } else if ("stop".equals(action)) {
            // 停止录制逻辑
            stopRecording(conferenceNo);
        }
    }

    private void startRecording(String conferenceNo) {
        // 实现录制开始逻辑
        // 可以使用MediaRecorder API或第三方录制服务
    }

    private void stopRecording(String conferenceNo) {
        // 实现录制停止逻辑
        // 保存录制文件等操作
    }

    private String getConferenceNo(WebSocketSession session) {
        return session.getAttributes().get("conferenceNo").toString();
    }

    private String getUserId(WebSocketSession session) {
        return session.getAttributes().get("userId").toString();
    }

    /**
     * 广播用户加入消息
     */
    private void broadcastUserJoin(String conferenceNo, String joinUserId) {
        Map<String, WebSocketSession> room = conferenceRooms.get(conferenceNo);
        if (room != null) {
            JSONObject joinMessage = new JSONObject();
            joinMessage.put("type", "user-joined");
            joinMessage.put("userId", joinUserId);

            // 向房间内的其他用户广播新用户加入的消息
            room.forEach((userId, session) -> {
                if (!userId.equals(joinUserId)) {
                    try {
                        session.sendMessage(new TextMessage(joinMessage.toJSONString()));
                    } catch (Exception e) {
                        log.error("广播用户加入消息失败", e);
                    }
                }
            });

            // 向新加入的用户发送当前房间内的其他用户列表
            JSONObject roomInfoMessage = new JSONObject();
            roomInfoMessage.put("type", "room-info");
            roomInfoMessage.put("users", room.keySet());

            try {
                room.get(joinUserId).sendMessage(new TextMessage(roomInfoMessage.toJSONString()));
            } catch (Exception e) {
                log.error("发送房间信息失败", e);
            }
        }
    }

    /**
     * 广播用户离开消息
     */
    private void broadcastUserLeave(String conferenceNo, String leaveUserId) {
        Map<String, WebSocketSession> room = conferenceRooms.get(conferenceNo);
        if (room != null) {
            JSONObject leaveMessage = new JSONObject();
            leaveMessage.put("type", "user-left");
            leaveMessage.put("userId", leaveUserId);

            // 向房间内的其他用户广播用户离开的消息
            room.forEach((userId, session) -> {
                if (!userId.equals(leaveUserId)) {
                    try {
                        session.sendMessage(new TextMessage(leaveMessage.toJSONString()));
                    } catch (Exception e) {
                        log.error("广播用户离开消息失败", e);
                    }
                }
            });
        }
    }
} 