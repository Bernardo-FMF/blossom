package org.blossom.auth.mapper.impl;

import org.blossom.auth.dto.UserDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements IDtoMapper<User, UserDto> {
    @Override
    public UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .token(entity.getPasswordResetToken().getToken())
                .expirationDate(entity.getPasswordResetToken().getExpirationDate())
                .build();
    }
}
