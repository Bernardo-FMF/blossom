package org.blossom.message.mapper;

import org.blossom.message.dto.MessageOperationDto;
import org.blossom.message.entity.Message;
import org.blossom.message.enums.BroadcastType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageOperationMapper {
    @Autowired
    private MessageDtoMapper messageDtoMapper;

    public MessageOperationDto mapToMessageOperationDto(Message message, BroadcastType broadcastType) {
        return MessageOperationDto.builder()
                .message(messageDtoMapper.mapToMessageDto(message))
                .type(broadcastType)
                .build();
    }
}
