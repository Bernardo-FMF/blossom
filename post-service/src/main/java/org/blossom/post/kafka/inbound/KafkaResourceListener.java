package org.blossom.post.kafka.inbound;

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
    private UserResourceHandler userResourceHandler;

    Map<EventType, Map<ResourceType, Consumer<KafkaResource>>> handlers = new HashMap<>();

    @PostConstruct
    private void initializeHandlers() {
        Map<ResourceType, Consumer<KafkaResource>> creationHandlers = new HashMap<>();
        Map<ResourceType, Consumer<KafkaResource>> updateHandlers = new HashMap<>();
        Map<ResourceType, Consumer<KafkaResource>> deleteHandlers = new HashMap<>();

        creationHandlers.put(ResourceType.USER, (event) -> userResourceHandler.save((KafkaUserResource) event));
        updateHandlers.put(ResourceType.USER, (event) -> userResourceHandler.update((KafkaUserResource) event));
        deleteHandlers.put(ResourceType.USER, (event) -> userResourceHandler.delete((KafkaUserResource) event));

        handlers.put(EventType.CREATE, creationHandlers);
        handlers.put(EventType.UPDATE, updateHandlers);
        handlers.put(EventType.DELETE, deleteHandlers);
    }

    @KafkaListener(topics = "user-resource-event-post")
    public void handleResource(ResourceEvent resourceEvent) {
        handlers.get(resourceEvent.getEventType()).get(resourceEvent.getResourceType()).accept(resourceEvent.getResource());
    }
}
