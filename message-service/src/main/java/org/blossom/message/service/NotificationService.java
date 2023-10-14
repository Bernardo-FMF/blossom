package org.blossom.message.service;

import org.blossom.message.entity.Message;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.kafka.outbound.KafkaMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    @Autowired
    private KafkaMessageService messageService;

    public void sendMessageNotification(Message message, BroadcastType broadcastType) {
        switch (broadcastType) {
            case MESSAGE_CREATED -> messageService.publishCreation(message);
            case MESSAGE_UPDATED -> messageService.publishUpdate(message);
            case MESSAGE_DELETED -> messageService.publishDelete(message);
            default -> {}
        }
    }
}
