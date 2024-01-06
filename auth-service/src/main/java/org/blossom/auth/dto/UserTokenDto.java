package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserTokenDto {
    private SimplifiedUserDto user;
    private TokenDto token;
}
