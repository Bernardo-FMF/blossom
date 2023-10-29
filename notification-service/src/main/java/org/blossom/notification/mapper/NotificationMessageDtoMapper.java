package org.blossom.notification.mapper;

import org.blossom.notification.dto.NotificationMessageDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.MessageNotification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageDtoMapper {
    public NotificationMessageDto mapToNotificationMessageDto(MessageNotification messageNotification, UserDto user) {
        return NotificationMessageDto.builder()
                .id(messageNotification.getMessageId())
                .user(user)
                .chatId(messageNotification.getChatId())
                .content(messageNotification.getContent())
                .sentAt(messageNotification.getSentAt())
                .isDeleted(messageNotification.isDeleted())
                .updatedAt(messageNotification.getUpdatedAt())
                .build();
    }
}
