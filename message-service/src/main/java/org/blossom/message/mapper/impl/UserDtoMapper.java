package org.blossom.message.mapper.impl;

import org.blossom.message.dto.UserDto;
import org.blossom.message.entity.User;
import org.blossom.message.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements IDtoMapper<User, UserDto> {
    @Override
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
