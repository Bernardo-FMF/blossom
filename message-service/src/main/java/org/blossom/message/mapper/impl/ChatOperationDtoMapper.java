package org.blossom.message.mapper.impl;

import org.blossom.message.dto.ChatOperationDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.mapper.interfac.ICompoundDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatOperationDtoMapper implements ICompoundDtoMapper<Chat, BroadcastType, ChatOperationDto> {
    @Autowired
    private ChatDtoMapper chatDtoMapper;

    @Override
    public ChatOperationDto toDto(Chat chat, BroadcastType broadcastType) {
        return ChatOperationDto.builder()
                .chat(chatDtoMapper.toDto(chat))
                .type(broadcastType)
                .build();
    }
}
