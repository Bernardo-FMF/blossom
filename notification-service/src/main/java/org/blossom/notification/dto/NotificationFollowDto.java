package org.blossom.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class NotificationFollowDto {
    private String id;
    private int userId;
    private UserDto follower;
    private Instant followedAt;
}