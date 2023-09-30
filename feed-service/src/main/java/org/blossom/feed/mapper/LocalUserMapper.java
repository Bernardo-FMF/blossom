package org.blossom.feed.mapper;

import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.entity.LocalUserPosts;
import org.blossom.model.KafkaUserResource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class LocalUserMapper {
    public LocalUser mapToLocalUser(KafkaUserResource source) {
        return LocalUser.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .username(source.getUsername())
                .imageUrl(source.getImageUrl())
                .build();
    }

    public LocalUserPosts mapToLocalUserPosts(int userId) {
        return LocalUserPosts.builder()
                .userId(userId)
                .posts(new ArrayList<>())
                .build();
    }
}
