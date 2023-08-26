package org.blossom.mapper;

import org.blossom.dto.InteractionDto;
import org.blossom.entity.Interaction;
import org.springframework.stereotype.Component;

@Component
public class InteractionDtoMapper {
    public InteractionDto mapToInteractionDto(Interaction interaction) {
        return InteractionDto.builder()
                .id(interaction.getId())
                .postId(interaction.getPostId())
                .userId(interaction.getUserId())
                .interactionType(interaction.getInteractionType())
                .build();
    }
}
