package org.blossom.message.mapper.impl;

import org.blossom.message.dto.ChatDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.mapper.interfac.IDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ChatDtoMapper implements IDtoMapper<Chat, ChatDto> {
    @Autowired
    private UserDtoMapper userDtoMapper;

    @Override
    public ChatDto toDto(Chat chat) {
        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .participants(chat.getParticipants().stream().map(participant -> userDtoMapper.toDto(participant)).collect(Collectors.toSet()))
                .owner(userDtoMapper.toDto(chat.getOwner()))
                .chatType(chat.getChatType())
                .build();
    }
}
