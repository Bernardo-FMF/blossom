package org.blossom.social.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FollowCountDto {
    private int id;
    private long totalFollows;
    private long totalFollowers;
}
