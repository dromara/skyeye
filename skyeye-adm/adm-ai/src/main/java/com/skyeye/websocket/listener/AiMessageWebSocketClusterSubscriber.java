/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.listener;

import com.skyeye.websocket.AiMessageWebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * AI WebSocket 跨节点分发消息订阅器
 */
@Component
public class AiMessageWebSocketClusterSubscriber implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AiMessageWebSocketClusterSubscriber.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody(), StandardCharsets.UTF_8);
            AiMessageWebSocket.handleClusterDispatch(payload);
        } catch (Exception e) {
            LOGGER.error("处理 Redis AI WebSocket 分发消息失败: {}", e.getMessage(), e);
        }
    }
}
