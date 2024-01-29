package org.blossom.message.mapper.impl;

import org.blossom.message.dto.ChatDto;
import org.blossom.message.dto.PaginationInfoDto;
import org.blossom.message.dto.UserChatsDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.mapper.interfac.IPaginatedDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserChatsDtoMapper implements IPaginatedDtoMapper<Chat, UserChatsDto, ChatDto, PaginationInfoDto> {
    @Autowired
    private ChatDtoMapper chatDtoMapper;

    @Override
    public UserChatsDto toPaginatedDto(Collection<Chat> chats, PaginationInfoDto paginationInfo) {
        return UserChatsDto.builder()
                .chats(chats.stream().map(this::toDto).collect(Collectors.toList()))
                .paginationInfo(paginationInfo)
                .build();
    }

    @Override
    public ChatDto toDto(Chat entity) {
        return chatDtoMapper.toDto(entity);
    }
}
