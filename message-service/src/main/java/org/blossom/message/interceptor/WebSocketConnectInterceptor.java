package org.blossom.message.interceptor;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.security.Principal;
import java.util.Optional;

@Log4j2
public class WebSocketConnectInterceptor<S> implements ApplicationListener<SessionConnectEvent> {
    public WebSocketConnectInterceptor(SimpMessageSendingOperations messagingTemplate) {
        super();
    }

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        Optional.ofNullable(readUser(event)).ifPresent(user -> log(event, user));
    }

    private void log(SessionConnectEvent event, Principal user) {
        String sessionId = readSessionId(event);
        log.info("User {} connected to session id {}", user.getName(), sessionId);
    }

    String readSessionId(SessionConnectEvent event) {
        return SimpMessageHeaderAccessor.getSessionId(event.getMessage().getHeaders());
    }

    Principal readUser(SessionConnectEvent event) {
        MessageHeaders headers = event.getMessage().getHeaders();
        return SimpMessageHeaderAccessor.getUser(headers);
    }
}
