package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.PaginationInfoDto;
import org.blossom.activity.dto.PostDto;
import org.blossom.activity.dto.UserInteractionsDto;
import org.blossom.activity.entity.Interaction;
import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.enums.InteractionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class UserInteractionsDtoMapper {
    @Autowired
    private InteractionDtoMapper interactionDtoMapper;

    public UserInteractionsDto toDto(LocalUser localUser, InteractionType interactionType, List<Interaction> entities, Map<String, PostDto> allPosts, PaginationInfoDto paginationInfo) {
        return UserInteractionsDto.builder()
                .user(localUser)
                .interactionType(interactionType)
                .interactions(entities.stream().map(interaction -> interactionDtoMapper.toDto(interaction, allPosts.get(interaction.getPostId()))).toList())
                .paginationInfo(paginationInfo)
                .build();

    }
}
