package org.blossom.message.controller;

import org.blossom.message.dto.ChatMessagesDto;
import org.blossom.message.dto.SearchParametersDto;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.exception.UserNotFoundException;
import org.blossom.message.service.MessageService;
import org.blossom.model.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping("/chat/{chatId}")
    public ResponseEntity<ChatMessagesDto> getChatMessages(@PathVariable("chatId") Integer chatId, SearchParametersDto searchParameters, Authentication authentication) throws UserNotFoundException, ChatNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.getChatMessages(chatId, searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}