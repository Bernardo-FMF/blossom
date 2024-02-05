package org.blossom.message.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.blossom.message.enums.BroadcastType;

import static org.blossom.message.json.JsonDtoParser.mapJsonMessage;
import static org.blossom.message.json.JsonDtoParser.mapJsonType;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageOperationDto {
    private MessageDto message;
    private BroadcastType type;

    @JsonCreator
    public MessageOperationDto(@JsonProperty("message") JsonNode message, @JsonProperty("type") String type) {
        this.message = mapJsonMessage(message);
        this.type = mapJsonType(type);
    }
}
