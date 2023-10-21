package org.blossom.social.kafka.outbound;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaPublisherFacade;
import org.blossom.model.*;
import org.blossom.social.kafka.outbound.model.SocialFollow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageService implements KafkaPublisherFacade<SocialFollow> {
    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Override
    public void publishCreation(SocialFollow entity) {
        KafkaSocialFollowResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.SOCIAL_FOLLOW, resource);

        kafkaTemplate.send(topic, resourceEvent);
    }

    @Override
    public void publishUpdate(SocialFollow entity) {
        throw new NotImplementedException("Follow updates are not available");
    }

    @Override
    public void publishDelete(SocialFollow entity) {
        KafkaSocialFollowResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.DELETE, ResourceType.SOCIAL_FOLLOW, resource);

        kafkaTemplate.send(topic, resourceEvent);
    }
}
