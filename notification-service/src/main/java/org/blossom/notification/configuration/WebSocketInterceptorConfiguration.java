package org.blossom.notification.configuration;

import org.blossom.notification.interceptor.WebSocketConnectInterceptor;
import org.blossom.notification.interceptor.WebSocketDisconnectInterceptor;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

@Configuration
public class WebSocketInterceptorConfiguration<S extends Session> {
    @Bean
    public WebSocketConnectInterceptor<S> webSocketConnectInterceptor(SimpMessageSendingOperations messagingTemplate) {
        return new WebSocketConnectInterceptor<>(messagingTemplate);
    }

    @Bean
    public WebSocketDisconnectInterceptor<S> webSocketDisconnectInterceptor(SimpMessageSendingOperations messagingTemplate) {
        return new WebSocketDisconnectInterceptor<>(messagingTemplate);
    }
}
