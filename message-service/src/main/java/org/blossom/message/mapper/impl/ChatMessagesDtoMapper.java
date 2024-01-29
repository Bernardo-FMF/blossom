package org.blossom.message.mapper.impl;

import org.blossom.message.dto.ChatMessagesDto;
import org.blossom.message.dto.MessageDto;
import org.blossom.message.dto.PaginationInfoDto;
import org.blossom.message.entity.Message;
import org.blossom.message.mapper.interfac.IPaginatedDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ChatMessagesDtoMapper implements IPaginatedDtoMapper<Message, ChatMessagesDto, MessageDto, PaginationInfoDto> {
    @Autowired
    private MessageDtoMapper messageDtoMapper;

    @Override
    public ChatMessagesDto toPaginatedDto(Collection<Message> entities, PaginationInfoDto paginationInfo) {
        return ChatMessagesDto.builder()
                .messageDtos(entities.stream().map(this::toDto).toList())
                .paginationInfo(paginationInfo)
                .build();
    }

    @Override
    public MessageDto toDto(Message entity) {
        return messageDtoMapper.toDto(entity);
    }
}
