package org.blossom.activity.dto;

import lombok.Builder;
import lombok.Getter;
import org.blossom.activity.enums.InteractionType;

@Builder
@Getter
public class InteractionDto {
    private int id;
    private PostDto post;
    private int userId;
    private InteractionType interactionType;
}
