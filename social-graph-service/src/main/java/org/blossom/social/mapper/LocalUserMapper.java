package org.blossom.social.mapper;

import org.blossom.social.dto.LocalUserDto;
import org.blossom.social.entity.GraphUser;
import org.springframework.stereotype.Component;

@Component
public class LocalUserMapper {
    public LocalUserDto mapToLocalUser(GraphUser source) {
        return LocalUserDto.builder()
                .id(source.getUserId())
                .fullName(source.getFullName())
                .username(source.getUsername())
                .imageUrl(source.getImageUrl())
                .build();
    }
}