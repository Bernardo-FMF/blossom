package org.blossom.notification.mapper.impl;

import org.blossom.notification.dto.NotificationMessageDto;
import org.blossom.notification.dto.UserDto;
import org.blossom.notification.entity.MessageNotification;
import org.blossom.notification.mapper.interfac.ICompoundDtoMapper;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageDtoMapper implements ICompoundDtoMapper<MessageNotification, UserDto, NotificationMessageDto> {
    @Override
    public NotificationMessageDto toDto(MessageNotification messageNotification, UserDto user) {
        return NotificationMessageDto.builder()
                .id(messageNotification.getId())
                .messageId(messageNotification.getMessageId())
                .user(user)
                .chatId(messageNotification.getChatId())
                .content(messageNotification.getContent())
                .sentAt(messageNotification.getSentAt())
                .isDeleted(messageNotification.isDeleted())
                .build();
    }
}
