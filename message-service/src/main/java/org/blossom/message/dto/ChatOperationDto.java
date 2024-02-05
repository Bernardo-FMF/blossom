package org.blossom.message.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.blossom.message.enums.BroadcastType;

import static org.blossom.message.json.JsonDtoParser.mapJsonChat;
import static org.blossom.message.json.JsonDtoParser.mapJsonType;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatOperationDto {
    private ChatDto chat;
    private BroadcastType type;

    @JsonCreator
    public ChatOperationDto(@JsonProperty("chat") JsonNode chat, @JsonProperty("type") String type) {
        this.chat = mapJsonChat(chat);
        this.type = mapJsonType(type);
    }
}
