package org.blossom.message.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatCreationDto {
    private List<Integer> initialParticipants;
    private boolean isGroup;
    private String name;
}
