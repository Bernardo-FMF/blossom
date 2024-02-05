package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {
    private int id;
    private String username;
    private String fullName;
    private String imageUrl;
}
