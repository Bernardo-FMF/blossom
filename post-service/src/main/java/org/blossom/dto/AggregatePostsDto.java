package org.blossom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AggregatePostsDto {
    private List<PostWithUserDto> posts;
    boolean eof;
    long currentPage;
    long totalPages;
    long totalElements;
}
