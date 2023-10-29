package org.blossom.notification.kafka;

import org.apache.commons.lang.NotImplementedException;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaSocialFollowResource;
import org.blossom.notification.client.UserClient;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.repository.FollowNotificationRepository;
import org.blossom.notification.service.BroadcastService;
import org.springframework.beans.factory.annotation.Autowired;

public class SocialFollowResourceHandler implements KafkaResourceHandler<KafkaSocialFollowResource> {
    @Autowired
    private FollowNotificationRepository followNotificationRepository;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private UserClient userClient;

    @Override
    public void save(KafkaSocialFollowResource resource) {
        FollowNotification followNotification = FollowNotification.builder()
                .senderId(resource.getInitiatingUser())
                .recipientId(resource.getReceivingUser())
                .followedAt(resource.getCreatedAt())
                .build();

        FollowNotification newFollowNotification = followNotificationRepository.save(followNotification);

        if (broadcastService.broadcastFollow(resource.getReceivingUser(), newFollowNotification)) {
            followNotification.setDelivered(true);
        }

        followNotificationRepository.save(newFollowNotification);
    }

    @Override
    public void update(KafkaSocialFollowResource resource) {
        throw new NotImplementedException("Follow updates are not available");
    }

    @Override
    public void delete(KafkaSocialFollowResource resource) {
        throw new NotImplementedException("Follow deletes are not available");
    }
}
