package org.blossom.message.service;

import org.blossom.message.entity.Chat;
import org.blossom.message.entity.Message;
import org.blossom.message.entity.User;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.mapper.impl.ChatOperationDtoMapper;
import org.blossom.message.mapper.impl.MessageOperationDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BroadcastService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RegistryService registryService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MessageOperationDtoMapper messageOperationDtoMapper;

    @Autowired
    private ChatOperationDtoMapper chatOperationDtoMapper;

    public void broadcastMessage(Set<User> usersInChat, Message message, BroadcastType type) {
        for (User user: usersInChat) {
            if (registryService.checkIfUserHasOpenConnection(user.getUsername())) {
                messagingTemplate.convertAndSendToUser(user.getUsername(), "/exchange/amq.direct/chat.message", messageOperationDtoMapper.toDto(message, type));
            } else {
                notificationService.sendMessageNotification(message, type);
            }
        }
    }

    public void broadcastChat(Chat chat, BroadcastType type) {
        for (User user: chat.getParticipants()) {
            if (registryService.checkIfUserHasOpenConnection(user.getUsername())) {
                messagingTemplate.convertAndSendToUser(user.getUsername(), "/exchange/amq.direct/chat", chatOperationDtoMapper.toDto(chat, type));
            }
        }
    }
}
