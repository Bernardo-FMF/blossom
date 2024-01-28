package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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
}