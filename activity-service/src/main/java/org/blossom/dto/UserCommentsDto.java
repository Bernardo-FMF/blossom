package org.blossom.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class UserCommentsDto {
    private int userId;
    private List<CommentDto> comments;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
