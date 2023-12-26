package org.blossom.post.mapper.impl;

import org.blossom.post.dto.GenericResponseDto;
import org.blossom.post.mapper.interfac.IGenericDtoMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GenericDtoMapper implements IGenericDtoMapper {
    @Override
    public GenericResponseDto toDto(String message, String id, Map<String, Object> metadata) {
        return GenericResponseDto.builder()
                .responseMessage(message)
                .resourceId(id)
                .metadata(metadata)
                .build();
    }
}
