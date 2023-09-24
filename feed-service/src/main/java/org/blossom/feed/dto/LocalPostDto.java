package org.blossom.feed.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LocalPostDto {
    private String id;
    private LocalUserDto creator;
    private String[] media;
    private String description;
    private MetadataDto metadata;
}
