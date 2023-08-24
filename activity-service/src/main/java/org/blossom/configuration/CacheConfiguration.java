package org.blossom.configuration;

import org.blossom.kafka.model.LocalPost;
import org.blossom.kafka.model.LocalUser;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class CacheConfiguration {
    @Value("${spring.redis.user-cache.host}")
    private String userHost;

    @Value("${spring.redis.user-cache.port}")
    private Integer userPort;

    @Value("${spring.redis.post-cache.host}")
    private String postHost;

    @Value("${spring.redis.post-cache.port}")
    private Integer postPort;

    @Bean(name = "redisConnectionFactoryUser")
    public RedisConnectionFactory redisConnectionFactoryUser() {
        return new LettuceConnectionFactory(userHost, userPort);
    }

    @Bean(name = "redisConnectionFactoryPost")
    public RedisConnectionFactory redisConnectionFactoryPost() {
        return new LettuceConnectionFactory(postHost, postPort);
    }

    @Bean(name = "redisTemplateUser")
    RedisTemplate<String, LocalUser> redisTemplateUser(@Qualifier("redisConnectionFactoryUser") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, LocalUser> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean(name = "redisTemplatePost")
    RedisTemplate<String, LocalPost> redisTemplatePost(@Qualifier("redisConnectionFactoryPost") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, LocalPost> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
