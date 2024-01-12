package org.blossom.social.mapper.impl;

import org.blossom.social.dto.PaginationInfoDto;
import org.blossom.social.dto.RecommendationsDto;
import org.blossom.social.dto.UserDto;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.mapper.interfac.IPaginatedDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RecommendationsDtoMapper implements IPaginatedDtoMapper<GraphUser, RecommendationsDto, UserDto, PaginationInfoDto> {
    @Autowired
    private UserDtoMapper userDtoMapper;

    public RecommendationsDto toPaginatedDto(int userId, Collection<GraphUser> entities, PaginationInfoDto paginationInfo) {
        RecommendationsDto paginatedDto = toPaginatedDto(entities, paginationInfo);
        paginatedDto.setUserId(userId);
        return paginatedDto;
    }

    @Override
    public UserDto toDto(GraphUser entity) {
        return userDtoMapper.toDto(entity);
    }

    @Override
    public RecommendationsDto toPaginatedDto(Collection<GraphUser> entities, PaginationInfoDto paginationInfo) {
        return RecommendationsDto.builder()
                .recommendations(entities.stream().map(this::toDto).toList())
                .paginationInfo(paginationInfo)
                .build();
    }
}
