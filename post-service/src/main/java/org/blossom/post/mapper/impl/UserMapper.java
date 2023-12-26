package org.blossom.post.mapper.impl;

import org.blossom.model.KafkaUserResource;
import org.blossom.post.dto.UserDto;
import org.blossom.post.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements IDtoMapper<KafkaUserResource, UserDto> {
    @Override
    public UserDto toDto(KafkaUserResource entity) {
        return UserDto.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
