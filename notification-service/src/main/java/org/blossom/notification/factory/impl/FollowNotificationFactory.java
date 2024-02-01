package org.blossom.notification.factory.impl;

import org.blossom.model.KafkaSocialFollowResource;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.factory.interfac.IEntityFactory;
import org.springframework.stereotype.Component;

@Component
public class FollowNotificationFactory implements IEntityFactory<FollowNotification, KafkaSocialFollowResource> {
    @Override
    public FollowNotification buildEntity(KafkaSocialFollowResource data) {
        return FollowNotification.builder()
                .senderId(data.getInitiatingUser())
                .recipientId(data.getReceivingUser())
                .followedAt(data.getCreatedAt())
                .build();
    }
}
