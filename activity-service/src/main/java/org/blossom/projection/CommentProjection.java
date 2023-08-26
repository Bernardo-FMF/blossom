package org.blossom.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

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
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;
    private Long replyCount;
}
