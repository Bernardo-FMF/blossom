package org.blossom.notification.mapper.impl;

import org.blossom.notification.dto.NotificationFollowDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.FollowNotification;
import org.blossom.notification.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationFollowDtoMapper implements ICompoundDtoMapper<FollowNotification, UserDto, NotificationFollowDto> {
    @Override
    public NotificationFollowDto toDto(FollowNotification followNotification, UserDto userDto) {
        return NotificationFollowDto.builder()
                .id(followNotification.getId())
                .follower(userDto)
                .followedAt(followNotification.getFollowedAt())
                .build();
    }
}
