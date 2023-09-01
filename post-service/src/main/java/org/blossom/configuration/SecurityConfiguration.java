package org.blossom.configuration;

import org.blossom.filter.CommonUserDetailsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    @Bean
    public CommonUserDetailsFilter commonAuthenticationFilter() {
        return new CommonUserDetailsFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(HttpMethod.POST, "/api/v1/post").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/post/{postId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/post/user/{userId}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/post/{postId}/identifier").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/post/{postId}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/post-search/simple-hashtag-lookup").permitAll())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(commonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
