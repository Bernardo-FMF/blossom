package org.blossom.message.factory.impl;

import org.blossom.message.dto.PublishMessageDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.Message;
import org.blossom.message.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MessageFactory {
    public Message buildEntity(Chat data, User data2, PublishMessageDto data3) {
        return Message.builder()
                .chat(data)
                .sender(data2)
                .content(data3.getContent())
                .createdAt(Instant.now())
                .build();
    }
}
