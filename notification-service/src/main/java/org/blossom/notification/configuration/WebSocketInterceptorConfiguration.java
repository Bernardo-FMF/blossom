package org.blossom.notification.configuration;

import org.blossom.notification.interceptor.WebSocketConnectInterceptor;
import org.blossom.notification.interceptor.WebSocketDisconnectInterceptor;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebSocketInterceptorConfiguration<S extends Session> {
    @Bean
    public WebSocketConnectInterceptor<S> webSocketConnectInterceptor() {
        return new WebSocketConnectInterceptor<>();
    }

    @Bean
    public WebSocketDisconnectInterceptor<S> webSocketDisconnectInterceptor() {
        return new WebSocketDisconnectInterceptor<>();
    }
}
