package org.blossom.notification.interceptor;

import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.blossom.model.CommonUserDetails;
import org.blossom.notification.cache.SessionCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.security.Principal;
import java.util.Optional;

@Log4j2
public class WebSocketConnectInterceptor<S> implements ApplicationListener<SessionConnectEvent> {
    @Autowired
    private SessionCacheService sessionCacheService;

    @Override
    public void onApplicationEvent(@Nullable SessionConnectEvent event) {
        Optional.ofNullable(readUser(event)).ifPresent(user -> {
            log(event, user);
            signalSessionConnect(readSessionId(event), (CommonUserDetails) ((UsernamePasswordAuthenticationToken) user).getPrincipal());
        });
    }

    private void signalSessionConnect(String sessionId, CommonUserDetails user) {
        sessionCacheService.addToCache(user.getUserId(), sessionId);
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
