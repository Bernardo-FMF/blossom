package org.blossom.auth.mapper.impl;

import org.blossom.auth.dto.TokenDto;
import org.blossom.auth.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class TokenDtoMapper implements ICompoundDtoMapper<String, String, TokenDto> {
    @Override
    public TokenDto toDto(String entity, String entity2) {
        return TokenDto.builder()
                .token(entity)
                .refreshToken(entity2)
                .build();
    }
}
