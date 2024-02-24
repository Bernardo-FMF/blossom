package org.blossom.gateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfiguration {
    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable).cors(cors -> cors.configurationSource(unused -> {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
            corsConfiguration.addAllowedHeader("content-type");
            corsConfiguration.addAllowedHeader("Authorization");
            if (allowedOrigins != null && allowedOrigins.length > 0) {
                corsConfiguration.setAllowedOrigins(List.of(allowedOrigins));
            } else {
                corsConfiguration.setAllowedOriginPatterns(List.of(CorsConfiguration.ALL));
            }
            return corsConfiguration;
        })).build();
    }
}