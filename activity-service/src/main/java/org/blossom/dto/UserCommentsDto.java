package org.blossom.dto;

import lombok.Builder;
import org.blossom.entity.LocalUser;

import java.util.List;

@Builder
public class UserCommentsDto {
    private LocalUser user;
    private List<CommentDto> comments;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
