package org.blossom.notification.repository;

import org.blossom.notification.entity.MessageNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageNotificationRepository extends MongoRepository<MessageNotification, String> {
    Page<MessageNotification> findByRecipientIdAndIsDeliveredFalse(int recipientId, Pageable pageable);
}
