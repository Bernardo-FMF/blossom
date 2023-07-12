package org.blossom.facade;

import org.blossom.model.KafkaResource;

public interface KafkaResourceHandler<T extends KafkaResource> {
    void save(T resource);
    void update(T resource);
    void delete(T resource);
}
