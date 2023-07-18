package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import org.blossom.model.KafkaUserResource;

public class KafkaUserResourceFactory implements KafkaResourceFactory<KafkaUserResource> {
    @Override
    public KafkaUserResource create(JsonNode json) {
        return KafkaUserResource.builder()
                .id(json.get("id").asInt())
                .userName(json.get("username").asText())
                .imageUrl(json.get("imageUrl") instanceof NullNode ? null : json.get("imageUrl").asText())
                .build();
    }
}
