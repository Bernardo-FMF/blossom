package org.blossom.notification.repository;

import org.blossom.notification.entity.FollowNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FollowNotificationRepository extends MongoRepository<FollowNotification, String> {
    Page<FollowNotification> findByRecipientId(int recipientId, Pageable pageable);
}
