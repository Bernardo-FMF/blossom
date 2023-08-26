package org.blossom.dto;

import lombok.Builder;
import org.blossom.enums.InteractionType;

@Builder
public class InteractionDto {
    private int id;
    private String postId;
    private int userId;
    private InteractionType interactionType;
}
