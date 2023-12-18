package org.blossom.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class GenericResponseDto {
    private String responseMessage;
    private Integer resourceId;
    private Map<String, Object> metadata;
}
