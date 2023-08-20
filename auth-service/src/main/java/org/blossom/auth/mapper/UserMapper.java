package org.blossom.auth.mapper;

import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public SimplifiedUserDto mapToSimplifiedUser(User user) {
        return SimplifiedUserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
