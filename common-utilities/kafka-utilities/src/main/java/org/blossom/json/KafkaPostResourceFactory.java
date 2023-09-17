package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.model.KafkaPostResource;

import java.util.ArrayList;

public class KafkaPostResourceFactory implements KafkaResourceFactory<KafkaPostResource> {
    @Override
    public KafkaPostResource create(JsonNode json) {
        return KafkaPostResource.builder()
                .id(json.get("id").asText())
                .userId(json.get("userId").asInt())
                .media(parseArray(json.get("media")))
                .description(json.get("description").asText())
                .build();
    }

    private String[] parseArray(JsonNode node) {
        ArrayList<String> values = new ArrayList<>(node.size());
        node.elements().forEachRemaining(x -> values.add(x.asText()));

        return values.toArray(String[]::new);
    }
}
