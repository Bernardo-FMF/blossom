package org.blossom.social.factory.impl;

import org.blossom.model.KafkaUserResource;
import org.blossom.social.entity.GraphUser;
import org.blossom.social.factory.interfac.IEntityFactory;
import org.springframework.stereotype.Component;

@Component
public class GraphUserFactory implements IEntityFactory<GraphUser, KafkaUserResource> {
    @Override
    public GraphUser buildEntity(KafkaUserResource data) {
        return GraphUser.builder()
                .userId(data.getId())
                .username(data.getUsername())
                .fullName(data.getFullName())
                .imageUrl(data.getImageUrl())
                .build();
    }
}
