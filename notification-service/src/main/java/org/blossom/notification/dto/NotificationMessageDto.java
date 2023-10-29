package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class NotificationMessageDto {
    private int id;
    private UserDto user;
    private int chatId;
    private String content;
    private boolean isDeleted;
    private Date sentAt;
}