package org.blossom.message.mapper.impl;

import org.blossom.message.dto.MessageDto;
import org.blossom.message.entity.Message;
import org.blossom.message.mapper.interfac.IDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageDtoMapper implements IDtoMapper<Message, MessageDto> {
    @Autowired
    private ChatDtoMapper chatDtoMapper;

    @Autowired
    private UserDtoMapper userDtoMapper;

    @Override
    public MessageDto toDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chat(chatDtoMapper.toDto(message.getChat()))
                .user(userDtoMapper.toDto(message.getSender()))
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .updatedAt(message.getUpdatedAt())
                .isEdited(message.getUpdatedAt() != null)
                .isDeleted(message.isDeleted())
                .build();
    }
}
