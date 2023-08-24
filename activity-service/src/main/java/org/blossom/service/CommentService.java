package org.blossom.service;

import jakarta.transaction.Transactional;
import org.blossom.cache.LocalPostCacheService;
import org.blossom.cache.LocalUserCacheService;
import org.blossom.dto.CommentInfoDto;
import org.blossom.dto.SearchParametersDto;
import org.blossom.dto.UpdatedCommentDto;
import org.blossom.dto.UserCommentsDto;
import org.blossom.entity.Comment;
import org.blossom.exception.CommentNotFoundException;
import org.blossom.exception.OperationNotAllowedException;
import org.blossom.exception.PostNotFoundException;
import org.blossom.exception.UserNotFoundException;
import org.blossom.mapper.CommentMapper;
import org.blossom.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private LocalPostCacheService localPostCache;

    @Autowired
    private CommentMapper commentMapper;

    @Transactional
    public Integer createComment(CommentInfoDto commentInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
        if (commentInfoDto.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(commentInfoDto.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        boolean parentExists = commentInfoDto.getParentComment() != null && commentRepository.existsById(commentInfoDto.getParentComment());
        if (!parentExists) {
            if (commentInfoDto.getParentComment() != null) {
                throw new CommentNotFoundException("Parent comment not found");
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

    public String deleteComment(int commentId, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }

        Comment comment = optionalComment.get();

        if (comment.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(comment.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        if (comment.isDeleted()) {
            return null;
        }

        comment.setDeleted(true);

        commentRepository.save(comment);

        return "Comment was deleted successfully";
    }

    public String updateComment(Integer commentId, UpdatedCommentDto updatedCommentDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }

        Comment comment = optionalComment.get();

        if (comment.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(comment.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        if (comment.isDeleted()) {
            return null;
        }

        comment.setCommentContent(updatedCommentDto.getNewContent());

        commentRepository.save(comment);

        return "Comment was updated successfully";
    }

    public UserCommentsDto getUserComments(int userId, SearchParametersDto searchParameters, int userId1) throws OperationNotAllowedException, UserNotFoundException {
        if (userId != userId1) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit()) : null;

        Page<Comment> comments = commentRepository.findByUserId(userId1, page);

        return UserCommentsDto.builder()
                .userId(userId1)
                .comments(comments.get().map(comment -> commentMapper.mapToCommentDto(comment)).toList())
                .totalPages(comments.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(comments.getTotalElements())
                .eof(!comments.hasNext())
                .build();
    }
}
