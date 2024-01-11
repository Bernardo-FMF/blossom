package org.blossom.post.mapper.impl;

import org.blossom.post.dto.PostWithUserDto;
import org.blossom.post.dto.UserDto;
import org.blossom.post.entity.Post;
import org.blossom.post.mapper.interfac.IDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class PostUserDtoMapper implements IDtoMapper<Post, PostWithUserDto> {
    public void setUser(PostWithUserDto postWithUserDto, UserDto userDto) {
        postWithUserDto.setUser(userDto);
    }

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
}
