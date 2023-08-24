package org.blossom.mapper;

import org.blossom.dto.CommentDto;
import org.blossom.entity.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .commentContent(comment.getCommentContent())
                .parentComment(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .build();
    }
}
