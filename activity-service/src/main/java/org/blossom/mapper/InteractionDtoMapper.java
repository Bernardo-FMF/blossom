package org.blossom.mapper;

import org.blossom.dto.InteractionDto;
import org.blossom.entity.Interaction;
import org.blossom.kafka.model.LocalPost;
import org.springframework.stereotype.Component;

@Component
public class InteractionDtoMapper {
    public InteractionDto mapToInteractionDto(Interaction interaction, LocalPost post) {
        return InteractionDto.builder()
                .id(interaction.getId())
                .postId(interaction.getPostId())
                .post(post)
                .userId(interaction.getUser().getId())
                .interactionType(interaction.getInteractionType())
                .build();
    }
}
