package org.blossom.notification.mapper;

import org.blossom.notification.dto.NotificationFollowDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.springframework.stereotype.Component;

@Component
public class NotificationFollowDtoMapper {
    public NotificationFollowDto mapToNotificationFollowDto(FollowNotification message, UserDto userDto) {
        return NotificationFollowDto.builder()
                .follower(userDto)
                .followedAt(message.getFollowedAt())
                .build();
    }
}
