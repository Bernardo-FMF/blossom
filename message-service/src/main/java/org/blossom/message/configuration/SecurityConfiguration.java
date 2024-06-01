package org.blossom.message.configuration;

import org.blossom.filter.CommonUserDetailsFilter;
import org.blossom.jwt.RoleParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Bean
    public RoleParser roleParser() {
        return new RoleParser();
    }

    @Bean
    public CommonUserDetailsFilter commonAuthenticationFilter() {
        return new CommonUserDetailsFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers("/ws-chat/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/v1/chat").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/chat/{chatId}/leave").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/v1/chat/{chatId}/user/{userId}").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/chat/{chatId}/user/{userId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/message/chat/{chatId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "api/v1/message-session").authenticated())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(commonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
