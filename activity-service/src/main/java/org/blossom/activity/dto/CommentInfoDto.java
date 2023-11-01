package org.blossom.activity.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentInfoDto {
    private int userId;
    private String postId;
    private String commentContent;
    private Integer parentComment;
}
