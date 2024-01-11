package org.blossom.post.mapper.impl;

import org.blossom.post.dto.MetadataDto;
import org.blossom.post.dto.PostDto;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class PostDtoMapper implements ICompoundDtoMapper<Post, MetadataDto, PostDto> {
    @Override
    public PostDto toDto(Post entity, MetadataDto entity2) {
        return PostDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .mediaUrls(entity.getMedia())
                .hashtags(entity.getHashtags())
                .metadata(entity2)
                .build();
    }
}
