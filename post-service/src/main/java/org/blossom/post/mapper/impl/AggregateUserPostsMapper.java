package org.blossom.post.mapper.impl;

import org.blossom.post.dto.AggregateUserPostsDto;
import org.blossom.post.dto.PaginationInfoDto;
import org.blossom.post.dto.PostDto;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.interfac.IPaginatedDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class AggregateUserPostsMapper implements IPaginatedDtoMapper<Post, AggregateUserPostsDto, PostDto, PaginationInfoDto> {
    @Autowired
    private PostMapper postMapper;

    public AggregateUserPostsDto toPaginatedDto(Collection<Post> entities, Integer userId, PaginationInfoDto paginationInfo) {
        AggregateUserPostsDto aggregateUserPostsDto = toPaginatedDto(entities, paginationInfo);
        aggregateUserPostsDto.setUserId(userId);
        return aggregateUserPostsDto;
    }

    @Override
    public PostDto toDto(Post entity) {
        return postMapper.toDto(entity);
    }

    @Override
    public AggregateUserPostsDto toPaginatedDto(Collection<Post> entities, PaginationInfoDto paginationInfo) {
        return AggregateUserPostsDto.builder()
                .posts(entities.stream().map(this::toDto).collect(Collectors.toList()))
                .paginationInfo(paginationInfo)
                .build();
    }
}
