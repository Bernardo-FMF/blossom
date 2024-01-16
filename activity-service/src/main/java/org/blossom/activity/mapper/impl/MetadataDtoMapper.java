package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.MetadataDto;
import org.blossom.activity.projection.CommentCountProjection;
import org.blossom.activity.projection.InteractionCountProjection;
import org.springframework.stereotype.Component;

@Component
public class MetadataDtoMapper {
    public MetadataDto toDto(Integer userId, String postId, InteractionCountProjection interactionCountProjection, CommentCountProjection commentCountProjection) {
        return MetadataDto.builder()
                .userId(userId)
                .postId(postId)
                .interactionMetadata(interactionCountProjection)
                .commentMetadata(commentCountProjection)
                .build();
    }
}
