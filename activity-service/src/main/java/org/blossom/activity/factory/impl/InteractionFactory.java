package org.blossom.activity.factory.impl;

import org.blossom.activity.dto.InteractionInfoDto;
import org.blossom.activity.entity.Interaction;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.enums.InteractionType;
import org.springframework.stereotype.Component;

@Component
public class InteractionFactory {
    public Interaction buildEntity(InteractionInfoDto interactionInfoDto, LocalUser localUser, InteractionType interactionType) {
        return Interaction.builder()
                .user(localUser)
                .postId(interactionInfoDto.getPostId())
                .interactionType(interactionType)
                .build();
    }
}
