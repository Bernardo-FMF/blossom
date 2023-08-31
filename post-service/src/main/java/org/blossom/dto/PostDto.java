package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class PostDto {
    private String id;
    private int userId;
    private String[] mediaUrls;
    private String[] hashtags;
    private String description;
    private Date createdAt;
}
