package org.blossom.notification.controller;

import org.blossom.model.CommonUserDetails;
import org.blossom.notification.dto.NotificationMessagesDto;
import org.blossom.notification.dto.SearchParametersDto;
import org.blossom.notification.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification/message")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<NotificationMessagesDto> getMessageNotifications(SearchParametersDto searchParameters, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.getMessageNotifications(searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @PatchMapping("/{notificationId}/received")
    public ResponseEntity<String> confirmUserReceivedMessage(@PathVariable("notificationId") String notificationId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.confirmUserReceivedMessage(notificationId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
