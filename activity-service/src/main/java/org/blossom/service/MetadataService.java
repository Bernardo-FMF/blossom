package org.blossom.service;

import org.blossom.dto.MetadataDto;
import org.blossom.projection.CommentCountProjection;
import org.blossom.projection.InteractionCountProjection;
import org.blossom.repository.CommentRepository;
import org.blossom.repository.InteractionRepository;
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