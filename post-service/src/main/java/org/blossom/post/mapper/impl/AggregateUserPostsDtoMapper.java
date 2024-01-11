package org.blossom.post.mapper.impl;

import org.blossom.post.dto.*;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.interfac.IPaginatedDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AggregateUserPostsDtoMapper implements IPaginatedDtoMapper<Post, AggregateUserPostsDto, PostDto, PaginationInfoDto> {
    @Autowired
    private PostDtoMapper postDtoMapper;

    @Autowired
    private MetadataDtoMapper metadataDtoMapper;

    public AggregateUserPostsDto toPaginatedDto(Collection<Post> entities, Integer userId, Map<String, MetadataDto> metadata, PaginationInfoDto paginationInfo) {
        return AggregateUserPostsDto.builder()
                .posts(entities.stream().map(post -> {
                    PostDto dto = toDto(post);
                    dto.setMetadata(metadata.get(post.getId()));
                    return dto;
                }).collect(Collectors.toList()))
                .userId(userId)
                .paginationInfo(paginationInfo)
                .build();
    }

    @Override
    public PostDto toDto(Post entity) {
        return postDtoMapper.toDto(entity, null);
    }

    @Override
    public AggregateUserPostsDto toPaginatedDto(Collection<Post> entities, PaginationInfoDto paginationInfo) {
        return AggregateUserPostsDto.builder()
                .posts(entities.stream().map(this::toDto).collect(Collectors.toList()))
                .paginationInfo(paginationInfo)
                .build();
    }
}
