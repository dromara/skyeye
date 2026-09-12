/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.websocket.config;

import com.skyeye.websocket.listener.AiMessageWebSocketClusterSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static com.skyeye.websocket.AiMessageWebSocket.WS_CLUSTER_CHANNEL;

/**
 * AI WebSocket 跨节点分发 Redis 监听配置
 */
@Configuration
public class AiMessageWebSocketRedisConfig {

    @Bean
    public RedisMessageListenerContainer aiWsRedisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory,
        AiMessageWebSocketClusterSubscriber subscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(subscriber, new ChannelTopic(WS_CLUSTER_CHANNEL));
        return container;
    }
}
