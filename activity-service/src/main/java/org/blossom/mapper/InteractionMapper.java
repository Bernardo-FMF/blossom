package org.blossom.mapper;

import org.blossom.dto.InteractionInfoDto;
import org.blossom.entity.Interaction;
import org.blossom.entity.LocalUser;
import org.blossom.enums.InteractionType;
import org.springframework.stereotype.Component;

@Component
public class InteractionMapper {
    public Interaction mapToInteraction(InteractionInfoDto interactionInfoDto, LocalUser localUser, InteractionType interactionType) {
        return Interaction.builder()
                .user(localUser)
                .postId(interactionInfoDto.getPostId())
                .interactionType(interactionType)
                .build();
    }
}
