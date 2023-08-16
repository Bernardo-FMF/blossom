package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SimplifiedUserDto {
    private String username;
    private String fullName;
    private String imageUrl;
}
