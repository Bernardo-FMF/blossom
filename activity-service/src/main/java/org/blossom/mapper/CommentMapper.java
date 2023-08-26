package org.blossom.mapper;

import org.blossom.dto.CommentInfoDto;
import org.blossom.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment mapToComment(CommentInfoDto comment) {
        return Comment.builder()
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .commentContent(comment.getCommentContent())
                .build();
    }
}
