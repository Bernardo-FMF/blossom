package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.model.KafkaPostResource;

import java.util.ArrayList;
import java.util.Date;

public class KafkaPostResourceFactory implements KafkaResourceFactory<KafkaPostResource> {
    @Override
    public KafkaPostResource create(JsonNode json) {
        return KafkaPostResource.builder()
                .id(json.get("id").asText())
                .userId(json.get("userId").asInt())
                .media(parseArray(json.get("media")))
                .description(json.get("description").asText())
                .createdAt(parseDate(json.get("createdAt")))
                .build();
    }

    private Date parseDate(JsonNode node) {
        return new Date(node.asLong());
    }

    private String[] parseArray(JsonNode node) {
        ArrayList<String> values = new ArrayList<>(node.size());
        node.elements().forEachRemaining(x -> values.add(x.asText()));

        return values.toArray(String[]::new);
    }
}
