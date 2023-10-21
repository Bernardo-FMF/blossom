package org.blossom.message.mapper;

import org.blossom.message.dto.ChatOperationDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.enums.BroadcastType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatOperationMapper {
    @Autowired
    private ChatDtoMapper chatDtoMapper;

    public ChatOperationDto mapToChatOperationDto(Chat chat, BroadcastType broadcastType) {
        return ChatOperationDto.builder()
                .chat(chatDtoMapper.mapToChatDto(chat))
                .broadcastType(broadcastType)
                .build();
    }
}
