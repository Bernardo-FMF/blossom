package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class PostDto {
    private String id;
    private int userId;
    private String[] mediaUrls;
    private String[] hashtags;
    private String description;
    private Instant createdAt;
    private MetadataDto metadata;
}
