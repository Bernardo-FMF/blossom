package org.blossom.notification.kafka;

import lombok.extern.log4j.Log4j2;
import org.blossom.facade.KafkaResourceHandler;
import org.blossom.model.KafkaMessageResource;
import org.blossom.notification.entity.MessageNotification;
import org.blossom.notification.factory.impl.MessageNotificationFactory;
import org.blossom.notification.repository.MessageNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class MessageResourceHandler implements KafkaResourceHandler<KafkaMessageResource> {
    @Autowired
    private MessageNotificationRepository messageNotificationRepository;

    @Autowired
    private MessageNotificationFactory messageNotification;

    @Override
    public void save(KafkaMessageResource resource) {
        log.info("processing save message of type message: {}", resource);

        Integer[] ids = Optional.ofNullable(resource.getRecipientsIds()).orElse(new Integer[0]);
        MessageNotification[] notifications = new MessageNotification[ids.length];

        for (int idx = 0; idx < ids.length; idx++) {
            notifications[idx] = messageNotification.buildEntity(resource, ids[idx]);
        }

        messageNotificationRepository.saveAll(List.of(notifications));
    }

    @Override
    public void update(KafkaMessageResource resource) {
        log.info("processing update message of type message: {}", resource);

        List<MessageNotification> notifications = messageNotificationRepository.findByMessageId(resource.getId());

        List<MessageNotification> newNotifications = notifications.stream().peek(notification -> {
            notification.setContent(resource.getContent());
        }).toList();

        messageNotificationRepository.saveAll(newNotifications);
    }

    @Override
    public void delete(KafkaMessageResource resource) {
        log.info("processing delete message of type message: {}", resource);

        List<MessageNotification> notifications = messageNotificationRepository.findByMessageId(resource.getId());

        messageNotificationRepository.deleteAll(notifications);
    }
}
