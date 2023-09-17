package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.model.KafkaPostResource;

public class KafkaPostResourceFactory implements KafkaResourceFactory<KafkaPostResource> {
    @Override
    public KafkaPostResource create(JsonNode json) {
        return KafkaPostResource.builder()
                .id(json.get("id").asText())
                .userId(json.get("userId").asInt())
                .media(null)
                .description(json.get("description").asText())
                .build();
    }
}
