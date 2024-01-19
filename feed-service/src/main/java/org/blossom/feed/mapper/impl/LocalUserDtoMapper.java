package org.blossom.feed.mapper.impl;

import org.blossom.feed.dto.LocalUserDto;
import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class LocalUserDtoMapper implements IDtoMapper<LocalUser, LocalUserDto> {
    @Override
    public LocalUserDto toDto(LocalUser localUser) {
        return LocalUserDto.builder()
                .id(localUser.getId())
                .username(localUser.getUsername())
                .fullName(localUser.getFullName())
                .imageUrl(localUser.getImageUrl())
                .build();
    }
}
