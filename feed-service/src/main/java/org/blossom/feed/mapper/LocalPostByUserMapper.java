package org.blossom.feed.mapper;

import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalPostByUserMapper {
    public LocalPostByUser mapToLocalPostUsers(KafkaPostResource post) {
        return LocalPostByUser.builder()
                .userId(post.getUserId())
                .postId(post.getId())
                .media(List.of(post.getMedia()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
