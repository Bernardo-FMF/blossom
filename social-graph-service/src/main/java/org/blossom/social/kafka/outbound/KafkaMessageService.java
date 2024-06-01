package org.blossom.social.kafka.outbound;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaPublisherFacade;
import org.blossom.model.EventType;
import org.blossom.model.KafkaSocialFollowResource;
import org.blossom.model.ResourceEvent;
import org.blossom.model.ResourceType;
import org.blossom.social.kafka.outbound.model.SocialFollow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class KafkaMessageService implements KafkaPublisherFacade<SocialFollow> {
    @Value("${spring.kafka.topics}")
    private String[] topics;

    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Override
    public void publishCreation(SocialFollow entity) {
        KafkaSocialFollowResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.SOCIAL_FOLLOW, resource);

        Arrays.stream(topics).forEach(topic -> kafkaTemplate.send(topic, resourceEvent));
    }

    @Override
    public void publishUpdate(SocialFollow entity) {
        throw new NotImplementedException("Follow updates are not available");
    }

    @Override
    public void publishDelete(SocialFollow entity) {
        throw new NotImplementedException("Follow deletes are not available");
    }
}
