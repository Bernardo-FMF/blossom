package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Builder
public class ChatDto {
    private int id;
    private String name;
    private Set<UserDto> participants;
    private UserDto owner;
    private boolean isGroup;
    private Instant lastUpdate;
}
