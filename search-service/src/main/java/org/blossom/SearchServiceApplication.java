package org.blossom;

import org.blossom.model.KafkaUserResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableDiscoveryClient
public class SearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }

    @KafkaListener(topics = "resource-event")
    public void handleNotification(ResourceEvent resourceEvent) {
        KafkaUserResource userResource = resourceEvent.getResourceType() == ResourceType.USER ? (KafkaUserResource) resourceEvent.getResource() : null;

        if (userResource == null) {
            return;
        }
        int userId = userResource.getId();
    }
}