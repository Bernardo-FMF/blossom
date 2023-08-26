package org.blossom.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GenericCreationDto {
    private int id;
    private String message;
}
