package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class UserDto {
    private int id;
    private String email;
    private String username;
    private String token;
    private Instant expirationDate;
}
