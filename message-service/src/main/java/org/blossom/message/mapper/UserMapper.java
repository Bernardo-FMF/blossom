package org.blossom.message.mapper;

import org.blossom.message.entity.User;
import org.blossom.model.KafkaUserResource;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User mapToUser(KafkaUserResource userResource) {
        return User.builder()
                .id(userResource.getId())
                .username(userResource.getUsername())
                .fullName(userResource.getFullName())
                .imageUrl(userResource.getImageUrl())
                .build();
    }
}
