package org.blossom.activity.service;

import org.blossom.activity.cache.LocalPostCacheService;
import org.blossom.activity.dto.MetadataDto;
import org.blossom.activity.dto.PostDto;
import org.blossom.activity.exception.PostNotFoundException;
import org.blossom.activity.mapper.impl.MetadataDtoMapper;
import org.blossom.activity.projection.CommentCountProjection;
import org.blossom.activity.projection.InteractionCountProjection;
import org.blossom.activity.repository.CommentRepository;
import org.blossom.activity.repository.InteractionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MetadataService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private LocalPostCacheService localPostCacheService;

    @Autowired
    private MetadataDtoMapper metadataDtoMapper;

    public MetadataDto getPostMetadata(String postId, Integer userId) throws PostNotFoundException {
        PostDto postDto = localPostCacheService.getFromCache(postId);
        if (postDto == null) {
            throw new PostNotFoundException("Post not found");
        }

        InteractionCountProjection interactionCountProjection;
        if (userId == null) {
            interactionCountProjection = interactionRepository.getInteractionCountWithNoUser(postId);
        } else {
            interactionCountProjection = interactionRepository.getInteractionCount(postId, userId);
        }

        CommentCountProjection commentCountProjection;
        if (userId == null) {
            commentCountProjection = commentRepository.getCommentCountWithNoUser(postId);
        } else {
            commentCountProjection = commentRepository.getCommentCount(postId, userId);
        }

        return metadataDtoMapper.toDto(userId, postId, interactionCountProjection, commentCountProjection);
    }
}