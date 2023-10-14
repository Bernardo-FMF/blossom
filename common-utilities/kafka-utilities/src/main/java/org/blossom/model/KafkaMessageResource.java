package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class KafkaMessageResource extends KafkaResource {
    private int id;
    private int senderId;
    private List<Integer> recipientsIds;
    private int chatId;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private boolean isDeleted;
}