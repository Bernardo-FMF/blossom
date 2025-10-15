package org.blossom.feed.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class LocalPostDto {
    private String id;
    private LocalUserDto user;
    private String[] media;
    private String[] hashtags;
    private String description;
    private Instant createdAt;
    private MetadataDto metadata;
}
