package org.blossom.feed.mapper;

import org.blossom.feed.dto.LocalUserDto;
import org.blossom.feed.entity.LocalUser;
import org.springframework.stereotype.Component;

@Component
public class LocalUserDtoMapper {
    public LocalUserDto mapToLocalUserDto(LocalUser localUser) {
        return LocalUserDto.builder()
                .id(localUser.getId())
                .username(localUser.getUsername())
                .fullName(localUser.getFullName())
                .imageUrl(localUser.getImageUrl())
                .build();
    }
}
