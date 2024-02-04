package org.blossom.message.mapper.impl;

import org.blossom.message.dto.ChatDto;
import org.blossom.message.dto.ChatMessagesDto;
import org.blossom.message.dto.MessageDto;
import org.blossom.message.dto.PaginationInfoDto;
import org.blossom.message.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ChatMessagesDtoMapper {
    @Autowired
    private MessageDtoMapper messageDtoMapper;

    public ChatMessagesDto toPaginatedDto(Collection<Message> entities, ChatDto chatDto, PaginationInfoDto paginationInfo) {
        return ChatMessagesDto.builder()
                .chat(chatDto)
                .messageDtos(entities.stream().map(this::toDto).toList())
                .paginationInfo(paginationInfo)
                .build();
    }

    private MessageDto toDto(Message entity) {
        return messageDtoMapper.toDto(entity);
    }
}
