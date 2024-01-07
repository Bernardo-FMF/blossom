package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoggedUserDto {
    private int id;
    private String username;
    private String fullName;
    private String imageUrl;
    private boolean mfaEnabled;
    private String email;
}
