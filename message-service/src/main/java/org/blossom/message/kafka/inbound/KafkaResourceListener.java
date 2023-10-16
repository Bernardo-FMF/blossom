package org.blossom.message.kafka.inbound;

import jakarta.annotation.PostConstruct;
import org.blossom.facade.KafkaConsumerFacade;
import org.blossom.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class KafkaResourceListener implements KafkaConsumerFacade {
    @Autowired
    private UserResourceHandler userResourceHandler;

    @Autowired
    private SocialFollowResourceHandler socialFollowResourceHandler;

    Map<EventType, Map<ResourceType, Consumer<KafkaResource>>> handlers = new HashMap<>();

    @PostConstruct
    private void initializeHandlers() {
        Map<ResourceType, Consumer<KafkaResource>> creationHandlers = new HashMap<>();
        Map<ResourceType, Consumer<KafkaResource>> updateHandlers = new HashMap<>();
        Map<ResourceType, Consumer<KafkaResource>> deleteHandlers = new HashMap<>();

        creationHandlers.put(ResourceType.USER, (event) -> userResourceHandler.save((KafkaUserResource) event));
        updateHandlers.put(ResourceType.USER, (event) -> userResourceHandler.update((KafkaUserResource) event));
        deleteHandlers.put(ResourceType.USER, (event) -> userResourceHandler.delete((KafkaUserResource) event));

        creationHandlers.put(ResourceType.SOCIAL_FOLLOW, (event) -> socialFollowResourceHandler.save((KafkaSocialFollowResource) event));
        updateHandlers.put(ResourceType.SOCIAL_FOLLOW, (event) -> socialFollowResourceHandler.update((KafkaSocialFollowResource) event));
        deleteHandlers.put(ResourceType.SOCIAL_FOLLOW, (event) -> socialFollowResourceHandler.delete((KafkaSocialFollowResource) event));

        handlers.put(EventType.CREATE, creationHandlers);
        handlers.put(EventType.UPDATE, updateHandlers);
        handlers.put(EventType.DELETE, deleteHandlers);
    }

    @KafkaListener(topics = {"user-resource-event-message", "social-follow-resource-event-message"})
    public void handleResource(ResourceEvent resourceEvent) {
        handlers.get(resourceEvent.getEventType()).get(resourceEvent.getResourceType()).accept(resourceEvent.getResource());
    }
}
