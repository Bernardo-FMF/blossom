package org.blossom.mapper;

import org.blossom.dto.CommentInfoDto;
import org.blossom.entity.Comment;
import org.blossom.entity.LocalUser;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    public Comment mapToComment(CommentInfoDto comment, LocalUser user) {
        return Comment.builder()
                .user(user)
                .postId(comment.getPostId())
                .commentContent(comment.getCommentContent())
                .build();
    }
}
