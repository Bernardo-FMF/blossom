package org.blossom.notification.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Builder
@Document(collection = "Blossom_Notification_Follow")
public class FollowNotification {
    @Id
    private String id;

    @Field(name = "recipient_id")
    private int recipientId;

    @Field(name = "sender_id")
    private int senderId;

    @Field(name = "followed_at")
    private Date followedAt;

    @Field(name = "is_delivered")
    private boolean isDelivered;
}