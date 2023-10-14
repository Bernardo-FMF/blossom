package org.blossom.message.dto;

import java.util.Date;

public class MessageDto {
    private int id;
    private ChatDto chat;
    private UserDto user;
    private String content;
    private Date createdAt;
    private boolean isEdited;
    private boolean isDeleted;
}
