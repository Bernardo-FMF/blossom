package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.enums.InteractionType;

@Builder
@Getter
public class InteractionDto {
    private int id;
    private String postId;
    private int userId;
    private InteractionType interactionType;
}
