package org.blossom.post.mapper.impl;

import org.blossom.post.dto.PostDto;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class PostMapper implements IDtoMapper<Post, PostDto> {
    @Override
    public PostDto toDto(Post entity) {
        return PostDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .mediaUrls(entity.getMedia())
                .hashtags(entity.getHashtags())
                .build();
    }
}
