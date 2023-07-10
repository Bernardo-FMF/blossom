package org.blossom.auth.kafka;

import org.blossom.auth.entity.User;
import org.blossom.model.EventType;
import org.blossom.model.KafkaUserResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageService {
    @Value("${spring.kafka.template.default-topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    public void sendUserCreationMessage(User user) {
        KafkaUserResource resource = user.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.USER, resource);

        kafkaTemplate.send(topic, resourceEvent);
    }
}
