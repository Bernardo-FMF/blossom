package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.model.KafkaResource;

public interface KafkaResourceFactory<T extends KafkaResource> {
    T create(JsonNode json);
}
