package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.kafka.model.LocalPost;

import java.util.List;

@Builder
@Getter
public class PostCommentsDto {
    private String postId;
    private LocalPost post;
    private List<CommentDto> comments;
    private boolean eof;
    private long currentPage;
    private long totalPages;
    private long totalElements;
}
