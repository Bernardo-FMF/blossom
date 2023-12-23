package org.blossom.social.mapper;

import org.blossom.social.dto.UserDto;
import org.blossom.social.entity.GraphUser;
import org.springframework.stereotype.Component;

@Component
public class LocalUserMapper {
    public UserDto mapToLocalUser(GraphUser source) {
        return UserDto.builder()
                .id(source.getUserId())
                .fullName(source.getFullName())
                .username(source.getUsername())
                .imageUrl(source.getImageUrl())
                .build();
    }
}