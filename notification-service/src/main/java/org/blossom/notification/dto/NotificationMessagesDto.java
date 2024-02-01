package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class NotificationMessagesDto {
    private int userId;
    private List<NotificationMessageDto> notificationMessages;
    private PaginationInfoDto paginationInfo;
}
