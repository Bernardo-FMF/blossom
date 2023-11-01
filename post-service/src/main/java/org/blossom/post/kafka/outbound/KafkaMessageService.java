package org.blossom.post.kafka.outbound;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.post.entity.Post;
import org.blossom.facade.KafkaPublisherFacade;
import org.blossom.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class KafkaMessageService implements KafkaPublisherFacade<Post> {
    @Value("${spring.kafka.topics}")
    private String[] topics;

    @Autowired
    private KafkaTemplate<String, ResourceEvent> kafkaTemplate;

    @Override
    public void publishCreation(Post entity) {
        KafkaPostResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.CREATE, ResourceType.POST, resource);

        Arrays.stream(topics).forEach(topic -> kafkaTemplate.send(topic, resourceEvent));
    }

    @Override
    public void publishUpdate(Post entity) {
        throw new NotImplementedException("Post updates are not available");
    }

    @Override
    public void publishDelete(Post entity) {
        KafkaPostResource resource = entity.mapToResource();
        ResourceEvent resourceEvent = new ResourceEvent(EventType.DELETE, ResourceType.POST, resource);

        Arrays.stream(topics).forEach(topic -> kafkaTemplate.send(topic, resourceEvent));
    }
}
