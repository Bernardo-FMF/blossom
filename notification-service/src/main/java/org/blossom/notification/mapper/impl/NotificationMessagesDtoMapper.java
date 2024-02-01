package org.blossom.notification.mapper.impl;

import org.blossom.notification.dto.NotificationMessageDto;
import org.blossom.notification.dto.NotificationMessagesDto;
import org.blossom.notification.dto.PaginationInfoDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.MessageNotification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class NotificationMessagesDtoMapper {
    @Autowired
    private NotificationMessageDtoMapper notificationMessageDtoMapper;

    public NotificationMessagesDto toDto(Collection<MessageNotification> notifications, Map<Integer, UserDto> users, PaginationInfoDto paginationInfoDto) {
        List<NotificationMessageDto> messages = notifications.stream().map(message -> notificationMessageDtoMapper.toDto(message, users.get(message.getSenderId()))).toList();

        return NotificationMessagesDto.builder()
                .notificationMessages(messages)
                .paginationInfo(paginationInfoDto)
                .build();
    }
}
