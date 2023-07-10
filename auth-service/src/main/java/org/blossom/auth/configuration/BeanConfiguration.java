package org.blossom.auth.configuration;

import org.blossom.jwt.RoleParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {
    @Bean
    public RoleParser roleParser() {
        return new RoleParser();
    }
}
