package org.blossom.notification.factory.impl;

import org.blossom.model.KafkaMessageResource;
import org.blossom.notification.entity.MessageNotification;
import org.blossom.notification.factory.interfac.ICompoundEntityFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageNotificationFactory implements ICompoundEntityFactory<MessageNotification, KafkaMessageResource, Integer> {
    @Override
    public MessageNotification buildEntity(KafkaMessageResource data, Integer data2) {
        return MessageNotification.builder()
                .messageId(data.getId())
                .chatId(data.getChatId())
                .content(data.getContent())
                .sentAt(data.getCreatedAt())
                .senderId(data.getSenderId())
                .recipientId(data2)
                .build();
    }
}
