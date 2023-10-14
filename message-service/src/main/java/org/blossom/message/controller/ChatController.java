package org.blossom.message.controller;

import org.blossom.message.dto.ChatCreationDto;
import org.blossom.message.dto.ChatDto;
import org.blossom.message.dto.SearchParametersDto;
import org.blossom.message.dto.UserChatsDto;
import org.blossom.message.enums.ChatType;
import org.blossom.message.exception.ChatNotFoundException;
import org.blossom.message.exception.IllegalChatOperationException;
import org.blossom.message.exception.InvalidChatException;
import org.blossom.message.exception.UserNotFoundException;
import org.blossom.message.service.ChatService;
import org.blossom.model.CommonUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatDto> createChat(ChatCreationDto chatCreation, Authentication authentication) throws InvalidChatException {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createChat(chatCreation, ((CommonUserDetails) authentication.getPrincipal()).getUserId(), ChatType.GROUP));
    }

    @DeleteMapping("/{chatId}/leave")
    public ResponseEntity<String> leaveChat(@PathVariable("chatId") Integer chatId, Authentication authentication) throws UserNotFoundException, ChatNotFoundException, IllegalChatOperationException {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.leaveChat(chatId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @GetMapping
    public ResponseEntity<UserChatsDto> getUserChats(SearchParametersDto searchParameters, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.getUserChats(searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @PutMapping("/{chatId}/user/{userId}")
    public ResponseEntity<String> addToChat(@PathVariable("chatId") Integer chatId, @PathVariable("userId") Integer userId, Authentication authentication) throws UserNotFoundException, ChatNotFoundException, IllegalChatOperationException {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.addToChat(chatId, userId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }


    @DeleteMapping("/{chatId}/user/{userId}")
    public ResponseEntity<String> removeFromChat(@PathVariable("chatId") Integer chatId, @PathVariable("userId") Integer userId, Authentication authentication) throws ChatNotFoundException, IllegalChatOperationException, UserNotFoundException {
        return ResponseEntity.status(HttpStatus.OK).body(chatService.removeFromChat(chatId, userId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
