package org.blossom.notification.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.blossom.notification.enums.BroadcastType;

import static org.blossom.notification.json.JsonDtoParser.mapJsonFollowNotification;
import static org.blossom.notification.json.JsonDtoParser.mapJsonType;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationFollowOperationDto {
    private NotificationFollowDto notification;
    private BroadcastType type;

    @JsonCreator
    public NotificationFollowOperationDto(@JsonProperty("notification") JsonNode notification, @JsonProperty("type") String type) {
        this.notification = mapJsonFollowNotification(notification);
        this.type = mapJsonType(type);
    }
}
