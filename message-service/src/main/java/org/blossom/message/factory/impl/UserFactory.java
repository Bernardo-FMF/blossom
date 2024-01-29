package org.blossom.message.factory.impl;

import org.blossom.message.entity.User;
import org.blossom.model.KafkaUserResource;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User buildEntity(KafkaUserResource data) {
        return User.builder()
                .id(data.getId())
                .username(data.getUsername())
                .fullName(data.getFullName())
                .imageUrl(data.getImageUrl())
                .build();
    }
}
