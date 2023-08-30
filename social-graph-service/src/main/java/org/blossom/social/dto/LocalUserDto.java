package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class LocalUserDto {
    private int id;
    private String fullName;
    private String username;
    private String imageUrl;
}