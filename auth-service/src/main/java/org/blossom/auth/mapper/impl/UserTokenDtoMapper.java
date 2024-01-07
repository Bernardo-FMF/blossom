package org.blossom.auth.mapper.impl;

import org.blossom.auth.dto.LoggedUserDto;
import org.blossom.auth.dto.TokenDto;
import org.blossom.auth.dto.UserTokenDto;
import org.blossom.auth.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserTokenDtoMapper implements ICompoundDtoMapper<LoggedUserDto, TokenDto, UserTokenDto> {
    public UserTokenDto toDto(LoggedUserDto entity) {
        LoggedUserDto placeholder = LoggedUserDto.builder()
                .email(entity.getEmail())
                .mfaEnabled(entity.isMfaEnabled())
                .build();

        return UserTokenDto.builder()
                .user(placeholder)
                .build();
    }

    @Override
    public UserTokenDto toDto(LoggedUserDto entity, TokenDto entity2) {
        return UserTokenDto.builder()
                .user(entity)
                .token(entity2)
                .build();
    }
}
