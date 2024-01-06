package org.blossom.auth.mapper.impl;

import org.blossom.auth.dto.UserDto;
import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.User;
import org.blossom.auth.entity.VerificationToken;
import org.blossom.auth.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper implements ICompoundDtoMapper<User, PasswordReset, UserDto> {
    @Override
    public UserDto toDto(User entity, PasswordReset entity2) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .token(entity2.getToken())
                .expirationDate(entity2.getExpirationDate())
                .build();
    }

    public UserDto toDto(User entity, VerificationToken entity2) {
        return UserDto.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .token(entity2.getToken())
                .expirationDate(entity2.getExpirationDate())
                .build();
    }
}
