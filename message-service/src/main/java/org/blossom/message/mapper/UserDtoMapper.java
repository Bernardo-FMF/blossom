package org.blossom.message.mapper;

import org.blossom.message.dto.UserDto;
import org.blossom.message.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
