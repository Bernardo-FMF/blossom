package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.activity.entity.LocalUser;

import java.util.List;

@Builder
@Getter
public class UserCommentsDto {
    private LocalUser user;
    private List<CommentDto> comments;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
