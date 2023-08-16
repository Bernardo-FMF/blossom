package org.blossom.auth.mapper;

import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.entity.Role;
import org.blossom.auth.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserDtoMapper {
    public User mapToUser(RegisterDto registerDto, Role role) {
        return User.builder()
                .email(registerDto.getEmail())
                .fullName(registerDto.getFullName())
                .username(registerDto.getUsername())
                .password(registerDto.getPassword())
                .roles(Set.of(role))
                .active(true)
                .build();
    }
}