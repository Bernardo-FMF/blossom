package org.blossom.message.service;

import org.blossom.message.entity.Chat;
import org.blossom.message.entity.Message;
import org.blossom.message.entity.User;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.mapper.ChatOperationMapper;
import org.blossom.message.mapper.MessageOperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BroadcastService {
    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RegistryService registryService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private MessageOperationMapper messageOperationMapper;

    @Autowired
    private ChatOperationMapper chatOperationMapper;

    public void broadcastMessage(int chatId, Message message, BroadcastType type) throws ChatNotFoundException {
        Set<User> usersInChat = chatService.getUsersInChat(chatId);
        for (User user: usersInChat) {
            if (registryService.checkIfUserHasOpenConnection(user.getUsername())) {
                messagingTemplate.convertAndSend("/topic/user/" + user.getId() + "/message", messageOperationMapper.mapToMessageOperationDto(message, type));
            } else {
                notificationService.sendMessageNotification(message, type);
            }
        }
    }

    public void broadcastChat(Chat chat, BroadcastType type) {
        for (User user: chat.getParticipants()) {
            if (registryService.checkIfUserHasOpenConnection(user.getUsername())) {
                messagingTemplate.convertAndSend("/topic/user/" + user.getId() + "/chat", chatOperationMapper.mapToChatOperationDto(chat, type));
            }
        }
    }


}
