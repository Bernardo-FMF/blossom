package org.blossom.activity.factory.impl;

import org.blossom.activity.dto.CommentInfoDto;
import org.blossom.activity.entity.Comment;
import org.blossom.activity.entity.LocalUser;
import org.springframework.stereotype.Component;

@Component
public class CommentFactory {
    public Comment buildEntity(CommentInfoDto comment, LocalUser user) {
        return Comment.builder()
                .user(user)
                .postId(comment.getPostId())
                .commentContent(comment.getCommentContent())
                .build();
    }
}
