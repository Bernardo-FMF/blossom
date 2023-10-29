package org.blossom.notification.controller;

import org.blossom.model.CommonUserDetails;
import org.blossom.notification.dto.NotificationFollowsDto;
import org.blossom.notification.dto.SearchParametersDto;
import org.blossom.notification.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notification/follow")
public class FollowController {
    @Autowired
    private FollowService followService;

    @GetMapping
    public ResponseEntity<NotificationFollowsDto> getFollowNotifications(SearchParametersDto searchParameters, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.getFollowNotifications(searchParameters, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }

    @PatchMapping("/{notificationId}/received")
    public ResponseEntity<String> confirmUserReceivedMessage(@PathVariable("notificationId") String notificationId, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.OK).body(followService.confirmUserReceivedNotification(notificationId, ((CommonUserDetails) authentication.getPrincipal()).getUserId()));
    }
}
