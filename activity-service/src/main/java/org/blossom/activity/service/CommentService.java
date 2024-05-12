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
import org.blossom.activity.factory.impl.CommentFactory;
import org.blossom.activity.mapper.impl.CommentDtoMapper;
import org.blossom.activity.mapper.impl.GenericDtoMapper;
import org.blossom.activity.mapper.impl.PostCommentsDtoMapper;
import org.blossom.activity.mapper.impl.UserCommentsDtoMapper;
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
    private CommentFactory commentFactory;

    @Autowired
    private LocalUserRepository localUserRepository;

    @Autowired
    private GenericDtoMapper genericDtoMapper;

    @Autowired
    private PostCommentsDtoMapper postCommentsDtoMapper;

    @Autowired
    private UserCommentsDtoMapper userCommentsDtoMapper;

    @Transactional
    public GenericResponseDto createComment(CommentInfoDto commentInfoDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
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

        Comment comment = commentFactory.buildEntity(commentInfoDto, optionalLocalUser.get());

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

        return genericDtoMapper.toDto("Comment was created successfully", newComment.getId(), null);
    }

    public GenericResponseDto deleteComment(int commentId, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
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
        comment.setCommentContent(null);

        commentRepository.save(comment);

        return genericDtoMapper.toDto("Comment was deleted successfully", commentId, null);
    }

    public GenericResponseDto updateComment(Integer commentId, UpdatedCommentDto updatedCommentDto, int userId) throws OperationNotAllowedException, UserNotFoundException, PostNotFoundException, CommentNotFoundException {
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

        return genericDtoMapper.toDto("Comment was updated successfully", commentId, null);
    }

    public UserCommentsDto getUserComments(SearchParametersDto searchParameters, int userId) throws UserNotFoundException {
        Optional<LocalUser> optionalLocalUser = localUserRepository.findById(userId);
        if (optionalLocalUser.isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.DESC, "createdAt")) : null;

        Page<Comment> comments = commentRepository.findByUserId(userId, page);

        PaginationInfoDto paginationInfo = new PaginationInfoDto(comments.getTotalPages(), searchParameters.getPage(), comments.getTotalElements(), !comments.hasNext());
        return userCommentsDtoMapper.toDto(optionalLocalUser.get(), comments.getContent(), paginationInfo);
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


        PaginationInfoDto paginationInfo = new PaginationInfoDto(comments.getTotalPages(), searchParameters.getPage(), comments.getTotalElements(), !comments.hasNext());
        return postCommentsDtoMapper.toDto(postDto, comments.getContent(), allUsers, paginationInfo);
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

        Pageable page = searchParameters.hasPagination() ? PageRequest.of(searchParameters.getPage(), searchParameters.getPageLimit(), Sort.by(Sort.Direction.ASC, "createdAt")) : Pageable.unpaged();

        Page<Comment> comments = commentRepository.findByTopLevelCommentId(commentId, page);

        PaginationInfoDto paginationInfo = new PaginationInfoDto(comments.getTotalPages(), searchParameters.getPage(), comments.getTotalElements(), !comments.hasNext());
        return postCommentsDtoMapper.toDto(postDto, comments.getContent(), paginationInfo);
    }
}
