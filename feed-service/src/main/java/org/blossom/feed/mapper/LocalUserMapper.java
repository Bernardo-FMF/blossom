package org.blossom.feed.mapper;

import org.blossom.feed.entity.LocalUser;
import org.blossom.model.KafkaUserResource;
import org.springframework.stereotype.Component;

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
}
