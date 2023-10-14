package org.blossom.message.kafka.outbound;

import org.blossom.facade.KafkaPublisherFacade;
import org.blossom.message.entity.Message;
import org.blossom.model.EventType;
import org.blossom.model.KafkaMessageResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageService implements KafkaPublisherFacade<Message> {
    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Override
    public void publishCreation(Message entity) {
        KafkaMessageResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.MESSAGE, resource);

        kafkaTemplate.send(topic, resourceEvent);
    }

    @Override
    public void publishUpdate(Message entity) {
        KafkaMessageResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.UPDATE, ResourceType.MESSAGE, resource);

        kafkaTemplate.send(topic, resourceEvent);
    }

    @Override
    public void publishDelete(Message entity) {
        KafkaMessageResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.DELETE, ResourceType.MESSAGE, resource);

        kafkaTemplate.send(topic, resourceEvent);
    }
}
