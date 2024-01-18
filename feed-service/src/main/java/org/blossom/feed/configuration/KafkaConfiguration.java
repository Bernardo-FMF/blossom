package org.blossom.feed.configuration;

import jakarta.annotation.PostConstruct;
import org.blossom.model.ResourceEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration(proxyBeanMethods = false)
public class KafkaConfiguration {
    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @PostConstruct
    void setup() {
        this.kafkaTemplate.setObservationEnabled(true);
    }
}