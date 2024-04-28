package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowRelationDto {
    boolean isSelf;
    boolean mutualFollow;
    boolean userFollows;
    boolean userIsFollowed;
}
