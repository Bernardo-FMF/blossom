package org.blossom.facade;

import org.blossom.model.KafkaEntity;

public interface KafkaPublisherFacade<T extends KafkaEntity> {
    void publishCreation(T entity);
    void publishUpdate(T entity);
    void publishDelete(T entity);
}
