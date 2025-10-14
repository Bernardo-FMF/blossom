package org.blossom.notification.configuration;

import org.blossom.notification.interceptor.WebSocketChannelInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Value("${broker.host}")
    private String brokerHost;

    @Value("${broker.port}")
    private int brokerPort;

    @Value("${broker.username}")
    private String brokerUser;

    @Value("${broker.password}")
    private String brokerPass;

    @Value("${websocket.origins}")
    private String[] allowedOrigins;

    @Value("${websocket.test.enabled}")
    private boolean testModeEnabled;

    @Autowired
    private WebSocketChannelInterceptor channelInterceptor;

    @Override
    public void registerStompEndpoints(final StompEndpointRegistry registry) {
        StompWebSocketEndpointRegistration stompWebSocketEndpointRegistration = registry.addEndpoint("/ws-notification")
                .setAllowedOriginPatterns(allowedOrigins);
        if (testModeEnabled) {
            stompWebSocketEndpointRegistration.withSockJS();
        }
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app").setUserDestinationPrefix("/user");
        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange")
                .setRelayHost(brokerHost)
                .setRelayPort(brokerPort)
                .setClientLogin(brokerUser)
                .setClientPasscode(brokerPass)
                .setSystemLogin(brokerUser)
                .setSystemPasscode(brokerPass)
                .setUserDestinationBroadcast("/topic/unresolved-user")
                .setUserRegistryBroadcast("/topic/log-user-registry");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(channelInterceptor);
    }
}
