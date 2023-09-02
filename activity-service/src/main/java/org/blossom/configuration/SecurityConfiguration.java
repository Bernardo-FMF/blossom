package org.blossom.configuration;

import org.blossom.filter.CommonUserDetailsFilter;
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
    public CommonUserDetailsFilter commonAuthenticationFilter() {
        return new CommonUserDetailsFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                        authorizationManagerRequestMatcherRegistry
                                .requestMatchers(HttpMethod.POST, "/api/v1/comment").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/v1/comment/{commentId}").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/v1/comment/{commentId}").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/v1/comment/user/self").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/v1/comment/{postId}").permitAll()
                                .requestMatchers("/api/v1/comment/{commendId}/replies").permitAll())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(commonAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
