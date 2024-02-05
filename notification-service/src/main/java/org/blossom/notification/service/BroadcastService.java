package org.blossom.notification.service;

import org.blossom.notification.client.AuthClient;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.enums.BroadcastType;
import org.blossom.notification.mapper.impl.NotificationFollowOperationDtoMapper;
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
    private NotificationFollowOperationDtoMapper notificationFollowOperationDtoMapper;

    public boolean broadcastFollow(int userId, FollowNotification notification, BroadcastType broadcastType) {
        ResponseEntity<UserDto> userResponse = authClient.getUser(userId);
        if (userResponse.getStatusCode().is2xxSuccessful()) {
            UserDto user = userResponse.getBody();
            if (user != null && user.getUsername() != null) {
                if (registryService.checkIfUserHasOpenConnection(user.getUsername())) {
                    ResponseEntity<UserDto> userResponse1 = authClient.getUser(notification.getSenderId());
                    if (userResponse1.getStatusCode().is2xxSuccessful()) {
                        messagingTemplate.convertAndSendToUser(user.getUsername(), "/exchange/amq.direct/notification.follow", notificationFollowOperationDtoMapper.toDto(notification, userResponse1.getBody(), broadcastType));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
