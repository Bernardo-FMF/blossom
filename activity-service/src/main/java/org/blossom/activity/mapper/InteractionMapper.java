package org.blossom.activity.mapper;

import org.blossom.activity.dto.InteractionInfoDto;
import org.blossom.activity.entity.Interaction;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.enums.InteractionType;
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
