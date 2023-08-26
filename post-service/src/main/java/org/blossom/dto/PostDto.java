package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class PostDto {
    private String id;
    private int userId;
    private String[] mediaUrls;
    private String[] hashtags;
    private String description;
    private Timestamp createdAt;
}
