package org.blossom.gateway.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.blossom.common.model.SimpleGrantedAuthorityDeserializer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BeanConfiguration {
    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(SimpleGrantedAuthority.class, new SimpleGrantedAuthorityDeserializer());

        objectMapper.registerModule(module);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector())
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configure -> configure.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper))).build())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    }
}