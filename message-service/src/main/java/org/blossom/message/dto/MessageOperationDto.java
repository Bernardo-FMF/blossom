package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.message.enums.BroadcastType;

@Getter
@Builder
public class MessageOperationDto {
    private MessageDto message;
    private BroadcastType type;
}
