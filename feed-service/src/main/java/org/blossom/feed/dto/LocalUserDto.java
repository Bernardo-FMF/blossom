package org.blossom.feed.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LocalUserDto {
    private int id;
    private String username;
    private String fullName;
    private String imageUrl;
}
