package org.blossom.message.mapper;

import org.blossom.message.dto.MessageDto;
import org.blossom.message.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageDtoMapper {
    @Autowired
    private ChatDtoMapper chatDtoMapper;

    @Autowired
    private UserDtoMapper userDtoMapper;

    public MessageDto mapToMessageDto(Message message) {
        return MessageDto.builder()
                .id(message.getId())
                .chat(chatDtoMapper.mapToChatDto(message.getChat()))
                .user(userDtoMapper.mapToUserDto(message.getSender()))
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isEdited(message.getUpdatedAt() != null)
                .isDeleted(message.isDeleted())
                .build();
    }
}
