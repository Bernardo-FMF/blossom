package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class PostWithUserDto {
    private String id;
    private UserDto user;
    private String[] mediaUrls;
    private String[] hashtags;
    private String description;
    private Date createdAt;
}
