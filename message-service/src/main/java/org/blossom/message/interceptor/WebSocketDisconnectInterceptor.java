package org.blossom.message.interceptor;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Log4j2
public class WebSocketDisconnectInterceptor<S> implements ApplicationListener<SessionDisconnectEvent> {
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        Optional.ofNullable(event.getUser()).ifPresent(user ->
                log.info("USer {} disconnected from session id {}", user.getName(), event.getSessionId())
        );
    }
}