package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.activity.projection.CommentCountProjection;
import org.blossom.activity.projection.InteractionCountProjection;

@Builder
@Getter
public class MetadataDto {
    private String postId;
    private Integer userId;
    private InteractionCountProjection interactionMetadata;
    private CommentCountProjection commentMetadata;
}
