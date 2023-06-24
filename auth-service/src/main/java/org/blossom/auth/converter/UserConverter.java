package org.blossom.auth.converter;

import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.entity.Role;
import org.blossom.auth.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
public class UserConverter implements Converter<RegisterDto, User> {
    @Override
    public User convert(RegisterDto registerDto) {
        return User.buildUser()
                .email(registerDto.getEmail())
                .username(registerDto.getUsername())
                .password(registerDto.getPassword())
                .active(true)
                .build();
    }

    public User convert(RegisterDto registerDto, Role role) {
        User user = convert(registerDto);
        Objects.requireNonNull(user).setRoles(Set.of(role));
        return user;
    }
}
