package org.blossom.mapper;

import org.blossom.dto.PostDto;
import org.blossom.dto.PostInfoDto;
import org.blossom.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostDtoMapper {
    public Post mapToPost(PostInfoDto post, String[] mediaUrls, String[] hashtags) {
        return Post.builder()
                .userId(post.getUserId())
                .description(post.getText())
                .media(mediaUrls)
                .hashtags(hashtags)
                .build();
    }

    public PostDto mapToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .description(post.getDescription())
                .createdAt(post.getCreatedAt())
                .mediaUrls(post.getMedia())
                .hashtags(post.getHashtags())
                .build();
    }
}
