package org.blossom.facade;

import org.blossom.model.ResourceEvent;

public interface KafkaConsumerFacade {
    void handleResource(ResourceEvent resourceEvent);
}
