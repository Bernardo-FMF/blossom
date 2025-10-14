package org.blossom.message.factory.impl;

import org.blossom.message.dto.ChatCreationDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.User;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class ChatFactory {
    public Chat buildEntity(Set<User> data, User data2, ChatCreationDto data3) {
        return Chat.builder()
                .owner(data2)
                .participants(data)
                .name(data3.getName())
                .isGroup(data3.isGroup())
                .lastUpdate(Instant.now())
                .build();
    }
}
