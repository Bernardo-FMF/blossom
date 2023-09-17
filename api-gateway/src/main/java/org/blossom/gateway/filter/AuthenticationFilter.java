package org.blossom.gateway.filter;

import org.blossom.model.dto.TokenDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<Config> {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String USER_ROLES = "userRoles";
    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClient;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(config.getHeaderName())) {
                    if (!validator.requiresAuthentication.test(exchange.getRequest())) {
                        return chain.filter(exchange);
                    }
                    throw new RuntimeException("Missing authorization header");
                }

                String authHeader = config.extractAuthorizationHeaderValue(exchange.getRequest().getHeaders());

                try {
                    String authUrl = "http://auth-service/api/v1/auth/validate?token=" + authHeader;
                    URI uri = UriComponentsBuilder.fromUriString(authUrl).build().toUri();

                    return webClient.build().get()
                            .uri(uri)
                            .accept(MediaType.APPLICATION_JSON)
                            .exchangeToMono(response -> {
                                if (response.statusCode().isError()) {
                                    return chain.filter(exchange);
                                }

                                return response.bodyToMono(TokenDto.class)
                                        .flatMap(tokenDto -> {
                                            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                                                    .header(USER_ID, String.valueOf(tokenDto.getUserId()))
                                                    .header(USERNAME, tokenDto.getUsername())
                                                    .header(USER_ROLES, StringUtils.collectionToCommaDelimitedString(tokenDto.getAuthorities()))
                                                    .build();
                                            return chain.filter(exchange.mutate().request(mutatedRequest).build());
                                        });
                            });
                } catch (Exception e) {
                    throw new RuntimeException("Unauthorized access to the application");
                }
            }
            return chain.filter(exchange);
        });
    }
}