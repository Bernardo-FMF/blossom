package org.blossom.post.mapper;

import org.blossom.post.dto.PostDto;
import org.blossom.post.dto.PostInfoDto;
import org.blossom.post.dto.PostWithUserDto;
import org.blossom.post.entity.Post;
import org.blossom.post.kafka.inbound.model.LocalUser;
import org.springframework.stereotype.Component;

@Component
public class PostDtoMapper {
    public Post mapToPost(PostInfoDto post, int userId, String[] mediaUrls, String[] hashtags) {
        return Post.builder()
                .userId(userId)
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

    public PostWithUserDto mapToPostWithUserDto(Post post, LocalUser user) {
        return PostWithUserDto.builder()
                .id(post.getId())
                .user(user)
                .description(post.getDescription())
                .createdAt(post.getCreatedAt())
                .mediaUrls(post.getMedia())
                .hashtags(post.getHashtags())
                .build();
    }
}
