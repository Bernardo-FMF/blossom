package org.blossom.notification.kafka;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.blossom.facade.KafkaConsumerFacade;
import org.blossom.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Component
@Log4j2
public class KafkaResourceListener implements KafkaConsumerFacade {
    @Autowired
    private SocialFollowResourceHandler socialFollowResourceHandler;

    @Autowired
    private MessageResourceHandler messageResourceHandler;

    Map<EventType, Map<ResourceType, Consumer<KafkaResource>>> handlers = new HashMap<>();

    @PostConstruct
    private void initializeHandlers() {
        Map<ResourceType, Consumer<KafkaResource>> creationHandlers = new HashMap<>();
        Map<ResourceType, Consumer<KafkaResource>> updateHandlers = new HashMap<>();
        Map<ResourceType, Consumer<KafkaResource>> deleteHandlers = new HashMap<>();

        creationHandlers.put(ResourceType.SOCIAL_FOLLOW, (event) -> socialFollowResourceHandler.save((KafkaSocialFollowResource) event));
        updateHandlers.put(ResourceType.SOCIAL_FOLLOW, (event) -> socialFollowResourceHandler.update((KafkaSocialFollowResource) event));
        deleteHandlers.put(ResourceType.SOCIAL_FOLLOW, (event) -> socialFollowResourceHandler.delete((KafkaSocialFollowResource) event));

        creationHandlers.put(ResourceType.MESSAGE, (event) -> messageResourceHandler.save((KafkaMessageResource) event));
        updateHandlers.put(ResourceType.MESSAGE, (event) -> messageResourceHandler.update((KafkaMessageResource) event));
        deleteHandlers.put(ResourceType.MESSAGE, (event) -> messageResourceHandler.delete((KafkaMessageResource) event));

        handlers.put(EventType.CREATE, creationHandlers);
        handlers.put(EventType.UPDATE, updateHandlers);
        handlers.put(EventType.DELETE, deleteHandlers);
    }

    @KafkaListener(topics = {"message-resource-event-notification", "social-follow-resource-event-notification"})
    public void handleResource(ResourceEvent resourceEvent) {
        log.debug("message received: {}", resourceEvent);

        handlers.get(resourceEvent.getEventType()).get(resourceEvent.getResourceType()).accept(resourceEvent.getResource());
    }
}
