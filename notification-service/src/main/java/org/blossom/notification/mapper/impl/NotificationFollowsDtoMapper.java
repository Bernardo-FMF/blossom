package org.blossom.notification.mapper.impl;

import org.blossom.notification.dto.NotificationFollowDto;
import org.blossom.notification.dto.NotificationFollowsDto;
import org.blossom.notification.dto.PaginationInfoDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class NotificationFollowsDtoMapper {
    @Autowired
    private NotificationFollowDtoMapper notificationFollowDtoMapper;

    public NotificationFollowsDto toDto(Collection<FollowNotification> notifications, Map<Integer, UserDto> users, PaginationInfoDto paginationInfoDto) {
        List<NotificationFollowDto> follows = notifications.stream().map(follow -> notificationFollowDtoMapper.toDto(follow, users.get(follow.getSenderId()))).toList();

        return NotificationFollowsDto.builder()
                .notificationFollows(follows)
                .paginationInfo(paginationInfoDto)
                .build();
    }
}
