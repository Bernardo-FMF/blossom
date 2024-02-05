package org.blossom.notification.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.notification.dto.NotificationFollowDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.enums.BroadcastType;

import java.time.Instant;

public class JsonDtoParser {
    private JsonDtoParser() {}

    public static BroadcastType mapJsonType(String type) {
        return BroadcastType.valueOf(type);
    }

    public static NotificationFollowDto mapJsonFollowNotification(JsonNode notification) {
        String id = notification.get("id").asText();
        int userId = notification.get("userId").asInt();
        UserDto follower = mapJsonUser(notification.get("follower"));

        Instant followedAt = Instant.ofEpochSecond(notification.get("followedAt").asLong());

        return NotificationFollowDto.builder()
                .id(id)
                .userId(userId)
                .follower(follower)
                .followedAt(followedAt)
                .build();
    }

    public static UserDto mapJsonUser(JsonNode user) {
        int id = user.get("id").asInt();
        String username = user.get("username").asText();
        String fullName = user.get("fullName").asText();
        String imageUrl = user.get("imageUrl").isNull() ? null : user.get("imageUrl").asText();

        return UserDto.builder()
                .id(id)
                .username(username)
                .fullName(fullName)
                .imageUrl(imageUrl)
                .build();
    }
}
