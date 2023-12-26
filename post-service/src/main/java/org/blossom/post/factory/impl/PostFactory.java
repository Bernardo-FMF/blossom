package org.blossom.post.factory.impl;

import org.blossom.post.dto.PostInfoDto;
import org.blossom.post.entity.Post;
import org.blossom.post.factory.interfac.IEntityFactory;
import org.springframework.stereotype.Component;

@Component
public class PostFactory implements IEntityFactory<Post, PostInfoDto> {
    @Override
    public Post buildEntity(PostInfoDto data) {
        return Post.builder()
                .userId(data.getUserId())
                .description(data.getText())
                .media(data.getMediaUrls())
                .hashtags(data.getHashtags())
                .build();
    }
}
