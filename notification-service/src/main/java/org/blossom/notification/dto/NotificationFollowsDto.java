package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class NotificationFollowsDto {
    private int userId;
    private List<NotificationFollowDto> notificationFollows;
    private PaginationInfoDto paginationInfo;
}
