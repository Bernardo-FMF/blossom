package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Builder
@Getter
public class MessageDto {
    private int id;
    private ChatDto chat;
    private UserDto user;
    private String content;
    private Date createdAt;
    private boolean isEdited;
    private boolean isDeleted;
}
