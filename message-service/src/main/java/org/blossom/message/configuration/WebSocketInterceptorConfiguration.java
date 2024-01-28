package org.blossom.message.configuration;

import org.blossom.message.interceptor.WebSocketConnectInterceptor;
import org.blossom.message.interceptor.WebSocketDisconnectInterceptor;
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
