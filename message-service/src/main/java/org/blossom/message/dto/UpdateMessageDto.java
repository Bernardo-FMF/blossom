package org.blossom.message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMessageDto {
    private int id;
    private String newContent;
}
