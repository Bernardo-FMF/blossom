package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.InteractionDto;
import org.blossom.activity.entity.Interaction;
import org.blossom.activity.dto.PostDto;
import org.blossom.activity.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class InteractionDtoMapper implements ICompoundDtoMapper<Interaction, PostDto, InteractionDto> {
    @Override
    public InteractionDto toDto(Interaction interaction, PostDto post) {
        return InteractionDto.builder()
                .id(interaction.getId())
                .post(post)
                .userId(interaction.getUser().getId())
                .interactionType(interaction.getInteractionType())
                .build();
    }
}
