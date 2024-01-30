package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.model.KafkaPostResource;

import java.time.Instant;
import java.util.ArrayList;

public class KafkaPostResourceFactory implements KafkaResourceFactory<KafkaPostResource> {
    @Override
    public KafkaPostResource create(JsonNode json) {
        return KafkaPostResource.builder()
                .id(json.get("id").asText())
                .userId(json.get("userId").asInt())
                .media(parseArray(json.get("media")))
                .hashtags(parseArray(json.get("hashtags")))
                .description(json.get("description").asText())
                .createdAt(parseDate(json.get("createdAt")))
                .build();
    }

    private Instant parseDate(JsonNode node) {
        return Instant.ofEpochSecond(node.asLong());
    }

    private String[] parseArray(JsonNode node) {
        ArrayList<String> values = new ArrayList<>(node.size());
        node.elements().forEachRemaining(elem -> values.add(elem.asText()));

        return values.toArray(String[]::new);
    }
}
