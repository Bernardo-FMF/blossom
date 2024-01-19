package org.blossom.feed.factory.impl;

import org.blossom.feed.entity.LocalUser;
import org.blossom.feed.factory.interfac.IEntityFactory;
import org.blossom.model.KafkaUserResource;
import org.springframework.stereotype.Component;

@Component
public class LocalUserFactory implements IEntityFactory<LocalUser, KafkaUserResource> {
    @Override
    public LocalUser buildEntity(KafkaUserResource data) {
        return LocalUser.builder()
                .id(data.getId())
                .fullName(data.getFullName())
                .username(data.getUsername())
                .imageUrl(data.getImageUrl())
                .build();
    }
}
