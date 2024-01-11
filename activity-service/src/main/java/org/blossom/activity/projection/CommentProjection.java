package org.blossom.activity.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Builder
@Setter
@Getter
@AllArgsConstructor
public class CommentProjection {
    private int id;
    private int userId;
    private String postId;
    private String commentContent;
    private Integer parentComment;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isDeleted;
    private Long replyCount;
}
