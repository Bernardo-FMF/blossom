package org.blossom.post.mapper.impl;

import org.blossom.post.dto.PostIdentifierDto;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class PostIdentifierMapper implements IDtoMapper<Post, PostIdentifierDto> {
    @Override
    public PostIdentifierDto toDto(Post entity) {
        return PostIdentifierDto.builder()
                .postId(entity.getId())
                .userId(entity.getUserId())
                .build();
    }
}
