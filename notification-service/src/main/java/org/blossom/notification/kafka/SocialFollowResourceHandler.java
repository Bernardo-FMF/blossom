package org.blossom.notification.kafka;

import lombok.extern.log4j.Log4j2;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaSocialFollowResource;
import org.blossom.notification.client.AuthClient;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.factory.impl.FollowNotificationFactory;
import org.blossom.notification.repository.FollowNotificationRepository;
import org.blossom.notification.service.BroadcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SocialFollowResourceHandler implements KafkaResourceHandler<KafkaSocialFollowResource> {
    @Autowired
    private FollowNotificationRepository followNotificationRepository;

    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private FollowNotificationFactory followNotificationFactory;

    @Override
    public void save(KafkaSocialFollowResource resource) {
        log.info("processing save message of type social follow: {}", resource);

        FollowNotification followNotification = followNotificationFactory.buildEntity(resource);

        FollowNotification newFollowNotification = followNotificationRepository.save(followNotification);

        if (broadcastService.broadcastFollow(resource.getReceivingUser(), newFollowNotification)) {
            followNotification.setDelivered(true);
        }

        followNotificationRepository.save(newFollowNotification);
    }

    @Override
    public void update(KafkaSocialFollowResource resource) {
        log.info("discarding update message of type social follow: {}", resource);
    }

    @Override
    public void delete(KafkaSocialFollowResource resource) {
        log.info("discarding delete message of type social follow: {}", resource);
    }
}
