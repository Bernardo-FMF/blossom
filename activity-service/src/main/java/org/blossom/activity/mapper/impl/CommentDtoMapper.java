package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.CommentDto;
import org.blossom.activity.entity.Comment;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.mapper.interfac.ICompoundDtoMapper;
import org.blossom.activity.mapper.interfac.IDtoMapper;
import org.blossom.activity.projection.CommentProjection;
import org.springframework.stereotype.Component;

@Component
public class CommentDtoMapper implements IDtoMapper<Comment, CommentDto>, ICompoundDtoMapper<CommentProjection, LocalUser, CommentDto> {
    @Override
    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .user(comment.getUser())
                .postId(comment.getPostId())
                .commentContent(comment.getCommentContent())
                .parentComment(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .isDeleted(comment.isDeleted())
                .build();
    }

    @Override
    public CommentDto toDto(CommentProjection entity, LocalUser entity2) {
        return CommentDto.builder()
                .id(entity.getId())
                .user(entity2)
                .userId(entity.getUserId())
                .postId(entity.getPostId())
                .commentContent(entity.getCommentContent())
                .parentComment(entity.getParentComment() != null ? entity.getParentComment() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .isDeleted(entity.getIsDeleted())
                .replyCount(entity.getReplyCount())
                .build();
    }
}
