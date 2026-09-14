package com.skyeye.eve.conference.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * 会议 WebSocket 配置。
 * <p>
 * 注意：{@link ServletServerContainerFactoryBean} 作用于整个 JVM 的 JSR-356 /
 * Tomcat WebSocket 容器（含 AI 的 {@code @ServerEndpoint}）。
 * 缓冲过大会按「每连接」预分配 HeapCharBuffer/ByteBuffer，导致堆内存被打爆
 * （adm-web OOM：27 连接 × ~30MB ≈ 810MB）。
 * <p>
 * 会议通道实际只转发 WebRTC 信令（offer/answer/ice）和聊天，媒体走 P2P，
 * 因此全局默认使用较小缓冲即可。
 */
@Configuration
@EnableWebSocket
public class ConferenceWebSocketConfig implements WebSocketConfigurer {

    /**
     * 全局默认文本/二进制缓冲。Tomcat 会按该值预分配每条连接的帧缓冲。
     * 256KB 足够 SDP/ICE 信令；切勿再设成 10MB 级全局默认。
     */
    private static final int DEFAULT_WS_BUFFER_SIZE = 256 * 1024;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(conferenceWebSocketHandler(), "/conference/websocket")
            .setAllowedOrigins("*");
    }

    @Bean
    public ConferenceWebSocketHandler conferenceWebSocketHandler() {
        return new ConferenceWebSocketHandler();
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(DEFAULT_WS_BUFFER_SIZE);
        container.setMaxBinaryMessageBufferSize(DEFAULT_WS_BUFFER_SIZE);
        return container;
    }
}
