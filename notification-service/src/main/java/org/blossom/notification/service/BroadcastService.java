package org.blossom.notification.service;

import org.blossom.notification.client.AuthClient;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.mapper.impl.NotificationFollowDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BroadcastService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RegistryService registryService;

    @Autowired
    private AuthClient authClient;

    @Autowired
    private NotificationFollowDtoMapper notificationFollowDtoMapper;

    public boolean broadcastFollow(int userId, FollowNotification notification) {
        ResponseEntity<UserDto> response = authClient.getUser(userId);
        if (response.getStatusCode().is2xxSuccessful()) {
            UserDto user = response.getBody();
            if (user != null && user.getUsername() != null) {
                if (registryService.checkIfUserHasOpenConnection(user.getUsername())) {
                    ResponseEntity<UserDto> response1 = authClient.getUser(notification.getSenderId());
                    if (response1.getStatusCode().is2xxSuccessful()) {
                        messagingTemplate.convertAndSendToUser(user.getUsername(), "/exchange/amq.direct/notification.follow", notificationFollowDtoMapper.toDto(notification, response1.getBody()));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
