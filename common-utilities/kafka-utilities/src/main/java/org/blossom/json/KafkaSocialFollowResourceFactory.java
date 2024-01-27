package org.blossom.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.model.KafkaSocialFollowResource;

public class KafkaSocialFollowResourceFactory implements KafkaResourceFactory<KafkaSocialFollowResource> {
    @Override
    public KafkaSocialFollowResource create(JsonNode json) {
        return KafkaSocialFollowResource.builder()
                .initiatingUser(json.get("initiatingUser").asInt())
                .receivingUser(json.get("receivingUser").asInt())
                .isMutualFollow(json.get("mutualFollow").asBoolean())
                .build();
    }
}
