package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class AggregateUserPostsDto {
    private int userId;
    private Set<PostDto> posts;
    private PaginationInfoDto paginationInfo;
}
