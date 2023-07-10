package org.blossom.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceEvent {
    private EventType eventType;
    private ResourceType resourceType;
    private KafkaResource resource;
}
