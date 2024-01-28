package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Builder
@Getter
public class MessageDto {
    private int id;
    private ChatDto chat;
    private UserDto user;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isEdited;
    private boolean isDeleted;
}
