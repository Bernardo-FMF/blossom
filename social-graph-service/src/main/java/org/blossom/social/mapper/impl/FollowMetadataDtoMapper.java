package org.blossom.social.mapper.impl;

import org.blossom.social.dto.FollowMetadataDto;
import org.blossom.social.dto.FollowRelationDto;
import org.springframework.stereotype.Component;

@Component
public class FollowMetadataDtoMapper {
    public FollowMetadataDto toDto(int userId, long followCount, long followerCount, FollowRelationDto followRelation) {
        return FollowMetadataDto.builder()
                .id(userId)
                .totalFollows(followCount)
                .totalFollowers(followerCount)
                .followRelation(followRelation)
                .build();
    }
}
