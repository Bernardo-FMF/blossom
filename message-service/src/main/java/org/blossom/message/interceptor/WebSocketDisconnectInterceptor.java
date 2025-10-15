package org.blossom.message.interceptor;

import lombok.extern.log4j.Log4j2;
import org.blossom.message.cache.SessionCacheService;
import org.blossom.model.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

@Log4j2
public class WebSocketDisconnectInterceptor<S> implements ApplicationListener<SessionDisconnectEvent> {
    @Autowired
    private SessionCacheService sessionCacheService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        Optional.ofNullable(event.getUser()).ifPresent(user -> {
            CommonUserDetails principal = (CommonUserDetails) ((Authentication) user).getPrincipal();
            log(event, principal);
            signalSessionDisconnect(event.getSessionId(), principal);
        });
    }

    private void signalSessionDisconnect(String sessionId, CommonUserDetails user) {
        sessionCacheService.deleteFromCache(user.getUserId(), sessionId);
    }

    private void log(SessionDisconnectEvent event, CommonUserDetails user) {
        String sessionId = event.getSessionId();
        log.info("User {} disconnected with session id {}", user.getUsername(), sessionId);
    }
}