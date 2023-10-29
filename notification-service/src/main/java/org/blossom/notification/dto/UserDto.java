package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    private int userId;
    private String username;
    private String fullName;
    private String imageUrl;
}
