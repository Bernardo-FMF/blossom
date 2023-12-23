package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class UserDto {
    private int id;
    private String fullName;
    private String username;
    private String imageUrl;
}