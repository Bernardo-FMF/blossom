package org.blossom.message.mapper;

import org.blossom.message.dto.ChatDto;
import org.blossom.message.entity.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ChatDtoMapper {
    @Autowired
    private UserDtoMapper userDtoMapper;

    public ChatDto mapToChatDto(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .participants(chat.getParticipants().stream().map(participant -> userDtoMapper.mapToUserDto(participant)).collect(Collectors.toSet()))
                .owner(userDtoMapper.mapToUserDto(chat.getOwner()))
                .build();
    }
}
