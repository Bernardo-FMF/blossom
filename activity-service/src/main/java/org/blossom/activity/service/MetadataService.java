package org.blossom.activity.service;

import org.blossom.activity.dto.MetadataDto;
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

    public MetadataDto getPostMetadata(String postId, Integer userId) {
        MetadataDto.MetadataDtoBuilder metadataBuilder = MetadataDto.builder();

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

        metadataBuilder
                .userId(userId)
                .postId(postId)
                .interactionMetadata(interactionCountProjection)
                .commentMetadata(commentCountProjection);

        return metadataBuilder.build();
    }
}