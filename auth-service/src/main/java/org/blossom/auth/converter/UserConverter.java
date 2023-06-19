package org.blossom.auth.converter;

import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public User convertRegisterToUser(RegisterDto registerDto) {
        return User.buildUser()
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .password(registerDto.getPassword())
                .build();
    }
}
