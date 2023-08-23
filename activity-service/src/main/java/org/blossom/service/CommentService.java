package org.blossom.service;

import jakarta.transaction.Transactional;
import org.blossom.dto.CommentInfoDto;
import org.blossom.dto.UpdatedCommentDto;
import org.blossom.dto.UserCommentsDto;
import org.blossom.entity.Comment;
import org.blossom.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Transactional
    public Integer createComment(CommentInfoDto commentInfoDto, int userId) {
        if (commentInfoDto.getUserId() != userId) {
            return null;
        }

        boolean parentExists = commentInfoDto.getParentComment() != null && commentRepository.existsById(commentInfoDto.getParentComment());
        if (!parentExists) {
            if (commentInfoDto.getParentComment() != null) {
                return null;
            }
        }

        Comment comment = Comment.builder()
                .userId(userId)
                .postId(commentInfoDto.getPostId())
                .commentContent(commentInfoDto.getCommentContent())
                .build();

        Comment newComment = commentRepository.save(comment);

        if (commentInfoDto.getParentComment() != null) {
            commentRepository.updateParentComment(newComment.getId(), commentInfoDto.getParentComment());
        }

        return newComment.getId();
    }

    public String deleteComment(int commentId, int userId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            return null;
        }

        Comment comment = optionalComment.get();

        if (comment.getUserId() != userId) {
            return null;
        }

        if (comment.isDeleted()) {
            return null;
        }

        comment.setDeleted(true);

        commentRepository.save(comment);

        return "Comment was deleted successfully";
    }

    public String updateComment(Integer commentId, UpdatedCommentDto updatedCommentDto, int userId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            return null;
        }

        Comment comment = optionalComment.get();

        if (comment.getUserId() != userId) {
            return null;
        }

        if (comment.isDeleted()) {
            return null;
        }

        comment.setCommentContent(updatedCommentDto.getNewContent());

        commentRepository.save(comment);

        return "Comment was updated successfully";
    }

    public UserCommentsDto getUserComments(Integer userId, int userId1) {
        return null;
    }
}
