package org.blossom.message.mapper.impl;

import org.blossom.message.dto.MessageOperationDto;
import org.blossom.message.entity.Message;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.mapper.interfac.ICompoundDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageOperationDtoMapper implements ICompoundDtoMapper<Message, BroadcastType, MessageOperationDto> {
    @Autowired
    private MessageDtoMapper messageDtoMapper;

    @Override
    public MessageOperationDto toDto(Message message, BroadcastType broadcastType) {
        return MessageOperationDto.builder()
                .message(messageDtoMapper.toDto(message))
                .type(broadcastType)
                .build();
    }
}
