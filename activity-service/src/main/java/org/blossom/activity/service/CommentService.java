package org.blossom.activity.service;

import jakarta.transaction.Transactional;
import org.blossom.activity.cache.LocalPostCacheService;
import org.blossom.activity.dto.*;
import org.blossom.activity.entity.Comment;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.exception.CommentNotFoundException;
import org.blossom.activity.exception.OperationNotAllowedException;
import org.blossom.activity.exception.PostNotFoundException;
import org.blossom.activity.exception.UserNotFoundException;
import org.blossom.activity.dto.PostDto;
import org.blossom.activity.mapper.CommentDtoMapper;
import org.blossom.activity.mapper.CommentMapper;
import org.blossom.activity.projection.CommentProjection;
import org.blossom.activity.repository.CommentRepository;
import org.blossom.activity.repository.LocalUserRepository;
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
    private LocalPostCacheService localPostCache;

    @Autowired
    private CommentDtoMapper commentDtoMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Transactional
    public GenericCreationDto createComment(CommentInfoDto commentInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
        if (commentInfoDto.getUserId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
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
                if (optionalParentComment.get().getParentComment() != null) {
                    throw new OperationNotAllowedException("Cannot reply to a reply");
                }
            } else {
                throw new CommentNotFoundException("Parent comment not found");
            }
        }

        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Comment comment = commentMapper.mapToComment(commentInfoDto, optionalLocalUser.get());

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

        if (comment.getUser().getId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
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

        if (comment.getUser().getId() != userId) {
            throw new OperationNotAllowedException("Logged in user cannot perform this operation");
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
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : null;

        Page<Comment> comments = commentRepository.findByUserId(userId, page);

        return UserCommentsDto.builder()
                .user(optionalLocalUser.get())
                .comments(comments.get().map(comment -> commentDtoMapper.mapToCommentDto(comment)).toList())
                .totalPages(comments.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(comments.getTotalElements())
                .eof(!comments.hasNext())
                .build();
    }

    public PostCommentsDto getPostComments(String postId, SearchParametersDto searchParameters) throws PostNotFoundException {
        PostDto postDto = localPostCache.getFromCache(postId);
        if (postDto == null) {
            throw new PostNotFoundException("Post not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : null;

        Page<CommentProjection> comments = commentRepository.findTopLevelCommentsWithReplyCount(postId, page);

        Map<Integer, LocalUser> allUsers = localUserRepository.findAllById(comments.get().map(CommentProjection::getUserId).toList()).stream()
                .collect(Collectors.toMap(LocalUser::getId, localUser -> localUser));

        return PostCommentsDto.builder()
                .post(postDto)
                .comments(comments.get().peek(comment -> comment.setCommentContent(comment.getIsDeleted() ? null : comment.getCommentContent())).map(comment -> commentDtoMapper.mapToCommentDto(comment, allUsers.get(comment.getUserId()))).toList())
                .totalPages(comments.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(comments.getTotalElements())
                .eof(!comments.hasNext())
                .build();
    }

    public PostCommentsDto getCommentReplies(Integer commentId, SearchParametersDto searchParameters) throws CommentNotFoundException, PostNotFoundException {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new CommentNotFoundException("Comment not found");
        }

        Comment comment = optionalComment.get();

        PostDto postDto = localPostCache.getFromCache(comment.getPostId());
        if (postDto == null) {
            throw new PostNotFoundException("Post not found");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : Pageable.unpaged();

        Page<Comment> comments = commentRepository.findByTopLevelCommentId(commentId, page);

        return PostCommentsDto.builder()
                .post(postDto)
                .comments(comments.get().map(comment1 -> commentDtoMapper.mapToCommentDto(comment1)).toList())
                .totalPages(comments.getTotalPages())
                .currentPage(searchParameters.getPage())
                .totalElements(comments.getTotalElements())
                .eof(!comments.hasNext())
                .build();
    }
}
