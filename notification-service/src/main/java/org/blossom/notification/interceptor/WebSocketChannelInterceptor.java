package org.blossom.notification.interceptor;

import jakarta.annotation.Nullable;
import lombok.extern.log4j.Log4j2;
import org.blossom.model.CommonUserDetails;
import org.blossom.notification.client.AuthClient;
import org.blossom.notification.dto.LocalTokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@Log4j2
public class WebSocketChannelInterceptor implements ChannelInterceptor {
    static final String AUTH_HEADER = "Authorization";
    static final String SESSION_KEY_HEADER = "simpSessionId";

    @Autowired
    private AuthClient authClient;

    @Override
    public Message<?> preSend(@Nullable Message<?> message, @Nullable MessageChannel channel) {
        final StompHeaderAccessor accessor = readHeaderAccessor(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            String authHeader = readAuthenticationHeader(accessor);
            String sessionId = readSessionId(accessor);

            ResponseEntity<LocalTokenDto> validate = authClient.validate(authHeader);
            if (validate.getStatusCode().is2xxSuccessful()) {
                LocalTokenDto body = validate.getBody();
                if (body == null) {
                    throw new AuthenticationCredentialsNotFoundException("Token body is not valid");
                }

                UserDetails userDetails = buildUserDetails(body.getUsername(), body.getUserId());

                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());

                accessor.setUser(authentication);
                accessor.setHeader("connection-time", LocalDateTime.now().toString());
                log.info("User with token '{}' and session {} made a WebSocket connection and generated the user {}", authHeader, sessionId, authentication.toString());
            }
        }

        return message;
    }

    private StompHeaderAccessor readHeaderAccessor(Message<?> message) {
        final StompHeaderAccessor accessor = getAccessor(message);
        if (accessor == null) {
            throw new AuthenticationCredentialsNotFoundException("Fail to read headers.");
        }
        return accessor;
    }

    private String readSessionId(StompHeaderAccessor accessor) {
        return Optional.ofNullable(accessor.getMessageHeaders().get(SESSION_KEY_HEADER))
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Session header not found")).toString();
    }

    private String readAuthenticationHeader(StompHeaderAccessor accessor) {
        final String authKey = accessor.getFirstNativeHeader(AUTH_HEADER);
        if (authKey == null || authKey.trim().isEmpty())
            throw new AuthenticationCredentialsNotFoundException("Auth Key Not Found");
        return authKey;
    }

    private UserDetails buildUserDetails(String username, Integer userId) {
        return new CommonUserDetails(userId, username, Set.of());
    }

    StompHeaderAccessor getAccessor(Message<?> message) {
        return MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
    }
}
