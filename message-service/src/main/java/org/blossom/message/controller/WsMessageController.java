package org.blossom.message.controller;

import org.blossom.message.dto.DeleteMessageDto;
import org.blossom.message.dto.PublishMessageDto;
import org.blossom.message.dto.UpdateMessageDto;
import org.blossom.message.entity.Message;
import org.blossom.message.enums.BroadcastType;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.exception.IllegalMessageOperationException;
import org.blossom.message.exception.MessageNotFoundException;
import org.blossom.message.exception.UserNotFoundException;
import org.blossom.message.service.BroadcastService;
import org.blossom.message.service.ChatService;
import org.blossom.message.service.MessageService;
import org.blossom.model.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class WsMessageController {
    @Autowired
    private BroadcastService broadcastService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/{chatId}/publishMessage")
    public void handlePublishMessage(@DestinationVariable int chatId, PublishMessageDto publishMessage, SimpMessageHeaderAccessor headerAccessor) throws ChatNotFoundException, UserNotFoundException {
        CommonUserDetails userDetails = ensureAuthentication(headerAccessor);

        Message message = messageService.createMessage(publishMessage, chatId, userDetails.getUserId());

        chatService.updateActivity(chatId);

        broadcastService.broadcastMessage(chatService.getUsersInChat(chatId), message, BroadcastType.MESSAGE_CREATED);
    }

    @MessageMapping("/chat/{chatId}/deleteMessage")
    public void handleDeleteMessage(@DestinationVariable int chatId, DeleteMessageDto deleteMessage, SimpMessageHeaderAccessor headerAccessor) throws ChatNotFoundException, MessageNotFoundException, IllegalMessageOperationException {
        CommonUserDetails userDetails = ensureAuthentication(headerAccessor);

        Message message = messageService.deleteMessage(deleteMessage, userDetails.getUserId());

        broadcastService.broadcastMessage(chatService.getUsersInChat(chatId), message, BroadcastType.MESSAGE_DELETED);
    }

    @MessageMapping("/chat/{chatId}/updateMessage")
    public void handleUpdateMessage(@DestinationVariable int chatId, UpdateMessageDto updateMessage, SimpMessageHeaderAccessor headerAccessor) throws ChatNotFoundException, MessageNotFoundException, IllegalMessageOperationException {
        CommonUserDetails userDetails = ensureAuthentication(headerAccessor);

        Message message = messageService.updateMessage(updateMessage, userDetails.getUserId());

        broadcastService.broadcastMessage(chatService.getUsersInChat(chatId), message, BroadcastType.MESSAGE_UPDATED);
    }

    private static CommonUserDetails ensureAuthentication(SimpMessageHeaderAccessor headerAccessor) {
        Authentication authentication = (Authentication) headerAccessor.getUser();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        return (CommonUserDetails) authentication.getPrincipal();
    }
}