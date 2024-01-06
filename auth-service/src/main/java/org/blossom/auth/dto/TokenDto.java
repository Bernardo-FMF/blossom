package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenDto {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
}
