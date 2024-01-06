package org.blossom.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.blossom.gateway.exception.MissingHeaderException;
import org.blossom.gateway.exception.UnauthorizedAccessException;
import org.blossom.gateway.exception.model.ErrorMessage;
import org.blossom.model.dto.ValidatedUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<Config> {

    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final String USER_ROLES = "userRoles";
    @Autowired
    private RouteValidator validator;

    @Autowired
    private WebClient.Builder webClient;

    @Autowired
    private ObjectMapper objectMapper;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            try {
                if (validator.isSecured.test(exchange.getRequest())) {
                    if (!exchange.getRequest().getHeaders().containsKey(config.getHeaderName())) {
                        if (!validator.requiresAuthentication.test(exchange.getRequest())) {
                            return chain.filter(exchange);
                        }
                        throw new MissingHeaderException("Missing authorization header");
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

                                    return response.bodyToMono(ValidatedUserDto.class)
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
                        throw new UnauthorizedAccessException("Unauthorized access to the application");
                    }
                }
                return chain.filter(exchange);
            } catch (Exception e1) {
                return handleError(exchange, e1);
            }
        });
    }

    private Mono<Void> handleError(ServerWebExchange exchange, Exception exception) {
        ServerHttpResponse response = exchange.getResponse();

        ErrorMessage errorMessage = buildError(exception);

        response.setStatusCode(errorMessage.getStatus());
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorMessage);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private ErrorMessage buildError(Exception exception) {
        HttpStatus status = null;
        if (exception instanceof MissingHeaderException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (exception instanceof UnauthorizedAccessException) {
            status = HttpStatus.UNAUTHORIZED;
        }
        return new ErrorMessage(status, MissingHeaderException.class.getName(), exception.getMessage(), new Date());
    }
}