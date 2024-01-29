package org.blossom.message.factory.impl;

import org.blossom.message.dto.ChatCreationDto;
import org.blossom.message.entity.Chat;
import org.blossom.message.entity.User;
import org.blossom.message.enums.ChatType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
public class ChatFactory {
    public Chat buildEntity(Set<User> data, User data2, ChatCreationDto data3, ChatType data4) {
        return Chat.builder()
                .owner(data2)
                .participants(data)
                .name(data3.getName())
                .chatType(data4)
                .lastUpdate(Instant.now())
                .build();
    }
}
