package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UserTokenDto {
    private LoggedUserDto user;
    private TokenDto token;
}
