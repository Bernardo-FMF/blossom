package org.blossom.notification.kafka;

import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaMessageResource;
import org.blossom.notification.entity.MessageNotification;
import org.blossom.notification.repository.MessageNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageResourceHandler implements KafkaResourceHandler<KafkaMessageResource> {
    @Autowired
    private MessageNotificationRepository messageNotificationRepository;

    @Override
    public void save(KafkaMessageResource resource) {
        Integer[] ids = Optional.ofNullable(resource.getRecipientsIds()).orElse(new Integer[0]);
        MessageNotification[] notifications = new MessageNotification[ids.length];

        for (int idx = 0; idx < ids.length; idx++) {
            MessageNotification messageNotification = MessageNotification.builder()
                    .messageId(resource.getId())
                    .chatId(resource.getChatId())
                    .content(resource.getContent())
                    .sentAt(resource.getCreatedAt())
                    .senderId(resource.getSenderId())
                    .recipientId(ids[idx])
                    .build();

            notifications[idx] = messageNotification;
        }

        messageNotificationRepository.saveAll(List.of(notifications));
    }

    @Override
    public void update(KafkaMessageResource resource) {
        List<MessageNotification> notifications = messageNotificationRepository.findByMessageId(resource.getId());

        List<MessageNotification> newNotifications = notifications.stream().peek(notification -> {
            notification.setContent(resource.getContent());
        }).toList();

        messageNotificationRepository.saveAll(newNotifications);
    }

    @Override
    public void delete(KafkaMessageResource resource) {
        List<MessageNotification> notifications = messageNotificationRepository.findByMessageId(resource.getId());

        List<MessageNotification> newNotifications = notifications.stream().peek(notification -> {
            notification.setDeleted(true);
            notification.setContent(null);
        }).toList();

        messageNotificationRepository.saveAll(newNotifications);
    }
}
