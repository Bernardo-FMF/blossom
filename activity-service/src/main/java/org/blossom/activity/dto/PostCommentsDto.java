package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PostCommentsDto {
    private PostDto post;
    private List<CommentDto> comments;
    private PaginationInfoDto paginationInfo;
}
