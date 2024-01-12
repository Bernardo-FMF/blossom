package org.blossom.social.mapper.impl;

import org.blossom.social.dto.UserDto;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements IDtoMapper<GraphUser, UserDto> {
    @Override
    public UserDto toDto(GraphUser source) {
        return UserDto.builder()
                .id(source.getUserId())
                .fullName(source.getFullName())
                .username(source.getUsername())
                .imageUrl(source.getImageUrl())
                .build();
    }
}