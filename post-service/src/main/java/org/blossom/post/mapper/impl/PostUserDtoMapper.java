package org.blossom.post.mapper.impl;

import org.blossom.post.dto.MetadataDto;
import org.blossom.post.dto.PostWithUserDto;
import org.blossom.post.dto.UserDto;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class PostUserDtoMapper implements IDtoMapper<Post, PostWithUserDto> {
    @Override
    public PostWithUserDto toDto(Post entity) {
        return PostWithUserDto.builder()
                .id(entity.getId())
                .user(null)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .mediaUrls(entity.getMedia())
                .hashtags(entity.getHashtags())
                .build();
    }

    public PostWithUserDto toDto(Post entity, UserDto entity2, MetadataDto entity3) {
        return PostWithUserDto.builder()
                .id(entity.getId())
                .user(entity2)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .mediaUrls(entity.getMedia())
                .hashtags(entity.getHashtags())
                .metadata(entity3)
                .build();
    }
}
