package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.model.KafkaMessageResource;

import java.time.Instant;
import java.util.ArrayList;

public class KafkaMessageResourceFactory implements KafkaResourceFactory<KafkaMessageResource> {
    @Override
    public KafkaMessageResource create(JsonNode json) {
        return KafkaMessageResource.builder()
                .id(json.get("id").asInt())
                .recipientsIds(parseArray(json.get("recipientsIds")))
                .senderId(json.get("senderId").asInt())
                .chatId(json.get("chatId").asInt())
                .content(json.get("content").asText())
                .createdAt(parseDate(json.get("createdAt")))
                .updatedAt(parseDate(json.get("updatedAt")))
                .isDeleted(json.get("isDeleted").asBoolean())
                .build();
    }

    private Integer[] parseArray(JsonNode node) {
        ArrayList<Integer> values = new ArrayList<>(node.size());
        node.elements().forEachRemaining(x -> values.add(x.asInt()));

        return values.toArray(Integer[]::new);
    }

    private Instant parseDate(JsonNode node) {
        if (node.isNull()) {
            return null;
        }
        return Instant.ofEpochSecond(node.asLong());
    }
}
