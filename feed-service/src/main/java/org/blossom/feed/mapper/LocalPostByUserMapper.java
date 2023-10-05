package org.blossom.feed.mapper;

import org.blossom.feed.entity.LocalPostByUser;
import org.springframework.stereotype.Component;

@Component
public class LocalPostByUserMapper {
    public LocalPostByUser mapToLocalPostUsers(int userId, String postId) {
        return LocalPostByUser.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }
}
