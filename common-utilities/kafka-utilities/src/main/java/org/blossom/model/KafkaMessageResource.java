package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;

@Getter
@Setter
@Builder
public class KafkaMessageResource extends KafkaResource {
    private int id;
    private int senderId;
    private Integer[] recipientsIds;
    private int chatId;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDeleted;

    @Override
    public String toString() {
        return "KafkaMessageResource(id=" + this.getId() +
                ", senderId=" + this.getSenderId() +
                ", recipientsIds=" + Arrays.deepToString(this.getRecipientsIds()) +
                ", chatId=" + this.getChatId() +
                ", content=" + this.getContent() +
                ", createdAt=" + this.getCreatedAt() +
                ", updatedAt=" + this.getUpdatedAt() +
                ", isDeleted=" + this.isDeleted() + ")";
    }
}