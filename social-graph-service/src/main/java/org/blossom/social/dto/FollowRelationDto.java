package org.blossom.social.dto;

import lombok.Builder;

@Builder
public class FollowRelationDto {
    boolean mutualFollow;
    boolean userFollows;
    boolean userIsFollowed;
}
