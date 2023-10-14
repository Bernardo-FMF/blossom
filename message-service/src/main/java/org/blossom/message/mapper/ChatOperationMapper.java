package org.blossom.message.mapper;

import org.blossom.message.dto.ChatOperationDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.enums.BroadcastType;
import org.springframework.stereotype.Component;

@Component
public class ChatOperationMapper {
    public ChatOperationDto mapToChatOperationDto(Chat chat, BroadcastType broadcastType) {
        return null;
    }
}
