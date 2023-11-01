package org.blossom.activity.mapper;

import org.blossom.activity.dto.InteractionDto;
import org.blossom.activity.entity.Interaction;
import org.blossom.activity.kafka.model.LocalPost;
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
