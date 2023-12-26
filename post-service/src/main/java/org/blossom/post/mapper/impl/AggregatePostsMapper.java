package org.blossom.post.mapper.impl;

import org.blossom.post.dto.AggregatePostsDto;
import org.blossom.post.dto.PaginationInfoDto;
import org.blossom.post.dto.PostWithUserDto;
import org.blossom.post.dto.UserDto;
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
    private PostUserMapper postUserMapper;

    public AggregatePostsDto toPaginatedDto(Collection<Post> entities, Map<Integer, UserDto> users, PaginationInfoDto paginationInfo) {
        return AggregatePostsDto.builder()
                .posts(entities.stream().map(post -> postUserMapper.setUser(toDto(post), users.get(post.getUserId()))).collect(Collectors.toList()))
                .paginationInfo(paginationInfo)
                .build();
    }

    @Override
    public PostWithUserDto toDto(Post entity) {
        return postUserMapper.toDto(entity);
    }

    @Override
    public AggregatePostsDto toPaginatedDto(Collection<Post> entities, PaginationInfoDto paginationInfo) {
        return AggregatePostsDto.builder()
                .posts(entities.stream().map(post -> postUserMapper.toDto(post)).collect(Collectors.toList()))
                .paginationInfo(paginationInfo)
                .build();
    }
}
