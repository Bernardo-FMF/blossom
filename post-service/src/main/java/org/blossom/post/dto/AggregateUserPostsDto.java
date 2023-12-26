package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AggregateUserPostsDto {
    private int userId;
    private List<PostDto> posts;
    private PaginationInfoDto paginationInfo;
}
