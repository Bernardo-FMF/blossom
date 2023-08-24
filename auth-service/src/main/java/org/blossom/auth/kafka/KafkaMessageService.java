package org.blossom.auth.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.auth.entity.User;
import org.blossom.facade.KafkaPublisherFacade;
import org.blossom.model.EventType;
import org.blossom.model.KafkaUserResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class KafkaMessageService implements KafkaPublisherFacade<User> {
    @Value("${spring.kafka.topics}")
    private String[] topics;

    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Override
    public void publishCreation(User entity) {
        KafkaUserResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.USER, resource);

        Arrays.stream(topics).forEach(topic -> kafkaTemplate.send(topic, resourceEvent));
    }

    @Override
    public void publishUpdate(User entity) {
        KafkaUserResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.UPDATE, ResourceType.USER, resource);

        Arrays.stream(topics).forEach(topic -> kafkaTemplate.send(topic, resourceEvent));
    }

    @Override
    public void publishDelete(User entity) {
        throw new NotImplementedException("User deletes are not available");
    }
}
