package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AggregateUserPostsDto {
    private int userId;
    private List<PostDto> posts;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
