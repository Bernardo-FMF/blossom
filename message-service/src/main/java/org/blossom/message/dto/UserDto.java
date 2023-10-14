package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
    private int id;
    private String username;
    private String fullName;
    private String imageUrl;
}
