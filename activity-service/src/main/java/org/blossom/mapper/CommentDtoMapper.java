package org.blossom.mapper;

import org.blossom.dto.CommentDto;
import org.blossom.entity.Comment;
import org.blossom.kafka.model.LocalUser;
import org.blossom.projection.CommentProjection;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper {
    public CommentDto mapToCommentDto(Comment comment, LocalUser user) {
        return CommentDto.builder()
                .id(comment.getId())
                .user(user)
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .commentContent(comment.isDeleted() ? null : comment.getCommentContent())
                .parentComment(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isDeleted(comment.isDeleted())
                .build();
    }

    public CommentDto mapToCommentDto(CommentProjection comment, LocalUser user) {
        return CommentDto.builder()
                .id(comment.getId())
                .user(user)
                .userId(comment.getUserId())
                .postId(comment.getPostId())
                .commentContent(comment.getIsDeleted() ? null : comment.getCommentContent())
                .parentComment(comment.getParentComment() != null ? comment.getParentComment() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isDeleted(comment.getIsDeleted())
                .build();
    }
}
