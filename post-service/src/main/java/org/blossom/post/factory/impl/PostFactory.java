package org.blossom.post.factory.impl;

import org.blossom.post.dto.PostInfoDto;
import org.blossom.post.entity.Post;
import org.blossom.post.factory.interfac.IEntityFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class PostFactory implements IEntityFactory<Post, PostInfoDto> {
    @Override
    public Post buildEntity(PostInfoDto data) {
        return Post.builder()
                .userId(data.getUserId())
                .description(data.getText())
                .media(Optional.ofNullable(data.getMediaUrls()).orElse(new String[] {}))
                .hashtags(Optional.ofNullable(data.getHashtags()).orElse(new String[] {}))
                .createdAt(Instant.now())
                .build();
    }
}
