package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.blossom.activity.entity.LocalUser;

import java.time.Instant;

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
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isDeleted;
    private Long replyCount;
}
