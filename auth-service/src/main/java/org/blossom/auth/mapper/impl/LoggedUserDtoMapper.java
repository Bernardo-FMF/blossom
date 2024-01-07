package org.blossom.auth.mapper.impl;

import org.blossom.auth.dto.LoggedUserDto;
import org.blossom.auth.entity.User;
import org.blossom.auth.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LoggedUserDtoMapper implements IDtoMapper<User, LoggedUserDto> {
    @Override
    public LoggedUserDto toDto(User entity) {
        return LoggedUserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .imageUrl(entity.getImageUrl())
                .email(entity.getEmail())
                .mfaEnabled(entity.isMfaEnabled())
                .build();
    }
}
