package org.blossom.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class GenericResponseDto {
    private String responseMessage;
    private String resourceId;
    private Map<String, Object> metadata;
}
