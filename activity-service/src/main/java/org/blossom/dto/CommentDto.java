package org.blossom.dto;

import lombok.Builder;
import lombok.Setter;
import org.blossom.entity.LocalUser;

import java.sql.Timestamp;

@Builder
@Setter
public class CommentDto {
    private int id;
    private int userId;
    private LocalUser user;
    private String postId;
    private String commentContent;
    private Integer parentComment;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;
    private Long replyCount;
}
