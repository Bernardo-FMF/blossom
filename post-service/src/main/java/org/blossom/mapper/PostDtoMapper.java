package org.blossom.mapper;

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
}
