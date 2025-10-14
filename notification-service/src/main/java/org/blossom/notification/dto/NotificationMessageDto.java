package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class NotificationMessageDto {
    private String id;
    private int messageId;
    private UserDto user;
    private int chatId;
    private String content;
    private boolean isDeleted;
    private Instant sentAt;
}