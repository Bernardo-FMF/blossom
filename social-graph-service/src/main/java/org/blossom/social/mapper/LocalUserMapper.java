package org.blossom.social.mapper;

import org.blossom.social.kafka.inbound.model.LocalUser;
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