package org.blossom.message.mapper;

import org.blossom.message.dto.MessageOperationDto;
import org.blossom.message.entity.Message;
import org.blossom.message.enums.BroadcastType;
import org.springframework.stereotype.Component;

@Component
public class MessageOperationMapper {
    public MessageOperationDto mapToMessageOperationDto(Message message, BroadcastType broadcastType) {
        return null;
    }
}
