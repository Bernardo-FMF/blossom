package org.blossom.auth.mapper.impl;

import org.blossom.auth.dto.SimplifiedUserDto;
import org.blossom.auth.dto.TokenDto;
import org.blossom.auth.dto.UserTokenDto;
import org.blossom.auth.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class UserTokenDtoMapper implements ICompoundDtoMapper<SimplifiedUserDto, TokenDto, UserTokenDto> {
    @Override
    public UserTokenDto toDto(SimplifiedUserDto entity, TokenDto entity2) {
        return UserTokenDto.builder()
                .user(entity)
                .token(entity2)
                .build();
    }
}
