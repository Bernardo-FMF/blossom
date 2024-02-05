package org.blossom.message.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.blossom.message.dto.ChatDto;
import org.blossom.message.dto.MessageDto;
import org.blossom.message.dto.UserDto;
import org.blossom.message.enums.BroadcastType;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public class JsonDtoParser {
    private JsonDtoParser() {}

    public static BroadcastType mapJsonType(String type) {
        return BroadcastType.valueOf(type);
    }

    public static MessageDto mapJsonMessage(JsonNode message) {
        int id = message.get("id").asInt();
        String content = message.get("content").asText();
        boolean edited = message.get("edited").asBoolean();
        boolean deleted = message.get("deleted").asBoolean();
        ChatDto chatDto = mapJsonChat(message.get("chat"));
        UserDto userDto = mapJsonUser(message.get("user"));

        Instant createdAt = Instant.ofEpochSecond(message.get("createdAt").asLong());
        Instant updatedAt = message.get("updatedAt").isNull() ? null : Instant.ofEpochSecond(message.get("updatedAt").asLong());

        return MessageDto.builder()
                .id(id)
                .content(content)
                .chat(chatDto)
                .user(userDto)
                .isEdited(edited)
                .isDeleted(deleted)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public static ChatDto mapJsonChat(JsonNode chat) {
        int id = chat.get("id").asInt();
        String name = chat.get("name").isNull() ? null : chat.get("name").asText();
        UserDto owner = mapJsonUser(chat.get("owner"));

        Set<UserDto> participants = new HashSet<>();
        chat.get("participants").elements().forEachRemaining(elem -> participants.add(mapJsonUser(elem)));

        return ChatDto.builder()
                .id(id)
                .name(name)
                .owner(owner)
                .participants(participants)
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
