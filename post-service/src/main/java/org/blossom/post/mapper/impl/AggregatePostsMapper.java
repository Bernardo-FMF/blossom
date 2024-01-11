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
public class AggregatePostsMapper implements IPaginatedDtoMapper<Post, AggregatePostsDto, PostWithUserDto, PaginationInfoDto> {
    @Autowired
    private PostUserDtoMapper postUserDtoMapper;

    public AggregatePostsDto toPaginatedDto(Collection<Post> entities, Map<Integer, UserDto> users, Map<String, MetadataDto> metadata, PaginationInfoDto paginationInfo) {
        return AggregatePostsDto.builder()
                .posts(entities.stream().map(post -> {
                    PostWithUserDto dto = toDto(post);
                    dto.setMetadata(metadata.get(post.getId()));
                    dto.setUser(users.get(post.getUserId()));
                    return dto;
                }).collect(Collectors.toList()))
                .paginationInfo(paginationInfo)
                .build();
    }

    @Override
    public PostWithUserDto toDto(Post entity) {
        return postUserDtoMapper.toDto(entity);
    }

    @Override
    public AggregatePostsDto toPaginatedDto(Collection<Post> entities, PaginationInfoDto paginationInfo) {
        return AggregatePostsDto.builder()
                .posts(entities.stream().map(post -> postUserDtoMapper.toDto(post)).collect(Collectors.toList()))
                .paginationInfo(paginationInfo)
                .build();
    }
}
