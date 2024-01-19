package org.blossom.feed.factory.impl;

import org.blossom.feed.entity.LocalPostByUser;
import org.blossom.feed.factory.interfac.IEntityFactory;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LocalPostByUserFactory implements IEntityFactory<LocalPostByUser, KafkaPostResource> {
    @Override
    public LocalPostByUser buildEntity(KafkaPostResource data) {
        return LocalPostByUser.builder()
                .userId(data.getUserId())
                .postId(data.getId())
                .media(List.of(data.getMedia()))
                .createdAt(data.getCreatedAt())
                .build();
        }
}