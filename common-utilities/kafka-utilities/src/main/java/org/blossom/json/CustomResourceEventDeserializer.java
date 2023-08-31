package org.blossom.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.blossom.model.*;

import java.io.IOException;

public class CustomResourceEventDeserializer implements Deserializer<ResourceEvent> {
    private final ObjectMapper objectMapper;

    public CustomResourceEventDeserializer() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ResourceEvent deserialize(String topic, byte[] data) {
        try {
            String json = new String(data);

            ResourceType resourceType = ResourceType.valueOf(objectMapper.readTree(json).get("resourceType").asText());
            EventType eventType = EventType.valueOf(objectMapper.readTree(json).get("eventType").asText());

            KafkaResource kafkaResource = buildFactory(resourceType).create(objectMapper.readTree(json).get("resource"));

            ResourceEvent resourceEvent = new ResourceEvent();
            resourceEvent.setResourceType(resourceType);
            resourceEvent.setEventType(eventType);
            resourceEvent.setResource(kafkaResource);

            return resourceEvent;
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to object: " + e.getMessage(), e);
        }
    }

    private KafkaResourceFactory<? extends KafkaResource> buildFactory(ResourceType resourceType) {
        return switch (resourceType) {
            case USER -> new KafkaUserResourceFactory();
            case POST -> new KafkaPostResourceFactory();
        };
    }
}