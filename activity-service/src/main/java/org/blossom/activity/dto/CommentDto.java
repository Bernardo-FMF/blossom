package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.blossom.activity.entity.LocalUser;

import java.sql.Timestamp;

@Builder
@Setter
@Getter
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
