package org.blossom.dto;

import lombok.Builder;

@Builder
public class CommentDto {
    private int id;
    private int userId;
    private String postId;
    private String commentContent;
    private Integer parentComment;
}
