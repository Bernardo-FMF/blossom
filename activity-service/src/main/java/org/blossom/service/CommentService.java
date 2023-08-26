package org.blossom.service;

import jakarta.transaction.Transactional;
import org.blossom.cache.LocalPostCacheService;
import org.blossom.cache.LocalUserCacheService;
import org.blossom.dto.*;
import org.blossom.entity.Comment;
import org.blossom.exception.CommentNotFoundException;
import org.blossom.exception.OperationNotAllowedException;
import org.blossom.exception.PostNotFoundException;
import org.blossom.exception.UserNotFoundException;
import org.blossom.kafka.model.LocalUser;
import org.blossom.mapper.CommentDtoMapper;
import org.blossom.mapper.CommentMapper;
import org.blossom.projection.CommentProjection;
import org.blossom.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LocalUserCacheService localUserCache;

    @Autowired
    private LocalPostCacheService localPostCache;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Transactional
    public GenericCreationDto createComment(CommentInfoDto commentInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
        if (commentInfoDto.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
        }

        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        if (!localPostCache.findEntry(commentInfoDto.getPostId())) {
            throw new PostNotFoundException("Post not found");
        }

        Optional<Comment> optionalParentComment = Optional.empty();
        if (commentInfoDto.getParentComment() != null) {
            optionalParentComment = commentRepository.findById(commentInfoDto.getParentComment());
            if (optionalParentComment.isPresent()) {
                if (!optionalParentComment.get().getPostId().equals(commentInfoDto.getPostId())) {
                    throw new OperationNotAllowedException("Parent comment mismatch");
                }
            } else {
                throw new CommentNotFoundException("Parent comment not found");
            }
        }

        Comment comment = commentMapper.mapToComment(commentInfoDto);

        Comment newComment = commentRepository.save(comment);

        if (commentInfoDto.getParentComment() != null) {
            Integer topLevelCommentId = null;
            if (optionalParentComment.isPresent()) {
                if (optionalParentComment.get().getTopLevelComment() != null) {
                    topLevelCommentId = optionalParentComment.get().getTopLevelComment().getId();
                } else {
                    topLevelCommentId = optionalParentComment.get().getId();
                }
            }
            commentRepository.updateParentComment(newComment.getId(), topLevelCommentId, commentInfoDto.getParentComment());
        }

        return GenericCreationDto.builder()
                .id(newComment.getId())
                .message("Comment was created successfully")
                .build();
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
            throw new OperationNotAllowedException("Comment is already deleted");
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

    public UserCommentsDto getUserComments(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        if (!localUserCache.findEntry(String.valueOf(userId))) {
            throw new UserNotFoundException("User not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : null;

        Page<Comment> comments = commentRepository.findByUserId(userId, page);

        return UserCommentsDto.builder()
                .user(localUserCache.getFromCache(String.valueOf(userId)))
                .comments(comments.get().map(comment -> commentDtoMapper.mapToCommentDto(comment, localUserCache.getFromCache(String.valueOf(userId)))).toList())
                .totalPages(comments.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(comments.getTotalElements())
                .eof(!comments.hasNext())
                .build();
    }

    public PostCommentsDto getPostComments(String postId, SearchParametersDto searchParameters) throws PostNotFoundException {
        if (!localPostCache.findEntry(postId)) {
            throw new PostNotFoundException("Post not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : null;

        Page<CommentProjection> comments = commentRepository.findTopLevelCommentsWithReplyCount(postId, page);

        Map<Integer, LocalUser> allUsers = localUserCache.getMultiFromCache(comments.get().map(comment -> String.valueOf(comment.getUserId())).toList()).stream()
                .collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        return PostCommentsDto.builder()
                .postId(postId)
                .comments(comments.get().peek(comment -> comment.setCommentContent(comment.getIsDeleted() ? null : comment.getCommentContent())).map(comment -> commentDtoMapper.mapToCommentDto(comment, allUsers.get(comment.getUserId()))).toList())
                .totalPages(comments.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(comments.getTotalElements())
                .eof(!comments.hasNext())
                .build();
    }

    public PostCommentsDto getCommentReplies(Integer commentId, SearchParametersDto searchParameters) throws CommentNotFoundException {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }

        Comment comment = optionalComment.get();

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : Pageable.unpaged();

        Page<Comment> comments = commentRepository.findByTopLevelCommentId(commentId, page);

        Map<Integer, LocalUser> allUsers = localUserCache.getMultiFromCache(comments.get().map(comment1 -> String.valueOf(comment1.getUserId())).toList()).stream()
                .collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        return PostCommentsDto.builder()
                .postId(comment.getPostId())
                .comments(comments.get().map(comment1 -> commentDtoMapper.mapToCommentDto(comment1, allUsers.get(comment1.getUserId()))).toList())
                .totalPages(comments.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(comments.getTotalElements())
                .eof(!comments.hasNext())
                .build();
    }
}
