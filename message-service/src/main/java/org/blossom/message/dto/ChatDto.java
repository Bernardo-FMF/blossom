package org.blossom.message.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class ChatDto {
    private int id;
    private String name;
    private Set<UserDto> participants;
    private UserDto owner;
}
