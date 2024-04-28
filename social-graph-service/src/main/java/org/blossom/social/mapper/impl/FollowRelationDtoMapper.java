package org.blossom.social.mapper.impl;

import org.blossom.social.dto.FollowRelationDto;
import org.springframework.stereotype.Component;

@Component
public class FollowRelationDtoMapper {
    public FollowRelationDto toDto(boolean userFollows, boolean userIsFollowed, boolean isSelf) {
        return FollowRelationDto.builder()
                .userIsFollowed(userIsFollowed)
                .userFollows(userFollows)
                .mutualFollow(userFollows && userIsFollowed)
                .isSelf(isSelf)
                .build();
    }
}
