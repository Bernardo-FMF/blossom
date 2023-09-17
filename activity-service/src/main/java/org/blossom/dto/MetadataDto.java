package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.projection.CommentCountProjection;
import org.blossom.projection.InteractionCountProjection;

@Builder
@Getter
public class MetadataDto {
    private String postId;
    private Integer userId;
    private InteractionCountProjection interactionMetadata;
    private CommentCountProjection commentMetadata;
}
