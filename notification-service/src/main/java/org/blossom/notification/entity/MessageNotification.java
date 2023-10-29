package org.blossom.notification.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@Builder
@Document(collection = "Blossom_Notification_Message")
public class MessageNotification {
    @Id
    private String id;

    @Field(name = "message_id")
    private int messageId;

    @Field(name = "recipient_id")
    private int recipientId;

    @Field(name = "sender_id")
    private int senderId;

    @Field(name = "chat_id")
    private int chatId;

    @Field(name = "content")
    private String content;

    @Field(name = "is_deleted")
    private boolean isDeleted;

    @Field(name = "sent_at")
    private Date sentAt;

    @Field(name = "is_delivered")
    private boolean isDelivered;
}
