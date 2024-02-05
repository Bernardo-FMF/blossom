package org.blossom.notification.mapper.impl;

import org.blossom.notification.dto.NotificationFollowOperationDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.enums.BroadcastType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationFollowOperationDtoMapper {
    @Autowired
    private NotificationFollowDtoMapper notificationFollowDtoMapper;

    public NotificationFollowOperationDto toDto(FollowNotification notification, UserDto user, BroadcastType broadcastType) {
        return NotificationFollowOperationDto.builder()
                .type(broadcastType)
                .notification(notificationFollowDtoMapper.toDto(notification, user))
                .build();
    }
}
