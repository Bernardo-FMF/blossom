package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.message.enums.BroadcastType;

@Builder
@Getter
public class ChatOperationDto {
    private ChatDto chat;
    private BroadcastType broadcastType;
}
