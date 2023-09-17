package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.enums.InteractionType;
import org.blossom.kafka.model.LocalPost;

@Builder
@Getter
public class InteractionDto {
    private int id;
    private String postId;
    private LocalPost post;
    private int userId;
    private InteractionType interactionType;
}
