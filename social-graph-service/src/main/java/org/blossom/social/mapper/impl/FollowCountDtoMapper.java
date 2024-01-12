package org.blossom.social.mapper.impl;

import org.blossom.social.dto.FollowCountDto;
import org.springframework.stereotype.Component;

@Component
public class FollowCountDtoMapper {
    public FollowCountDto toDto(int userId, long followCount, long followerCount) {
        return FollowCountDto.builder()
                .id(userId)
                .totalFollows(followCount)
                .totalFollowers(followerCount)
                .build();
    }
}
