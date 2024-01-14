package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowMetadataDto {
    private int id;
    private long totalFollows;
    private long totalFollowers;
    private FollowRelationDto followRelation;
}
