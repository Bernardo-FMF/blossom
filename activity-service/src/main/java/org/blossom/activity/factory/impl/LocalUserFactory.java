package org.blossom.activity.factory.impl;

import org.blossom.activity.entity.LocalUser;
import org.blossom.activity.factory.interfac.IEntityFactory;
import org.blossom.model.KafkaUserResource;
import org.springframework.stereotype.Component;

@Component
public class LocalUserFactory implements IEntityFactory<LocalUser, KafkaUserResource> {
    @Override
    public LocalUser buildEntity(KafkaUserResource entity) {
        return LocalUser.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .username(entity.getUsername())
                .imageUrl(entity.getImageUrl())
                .build();
    }
}
