package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class NotificationFollowDto {
    private UserDto follower;
    private Date followedAt;
}