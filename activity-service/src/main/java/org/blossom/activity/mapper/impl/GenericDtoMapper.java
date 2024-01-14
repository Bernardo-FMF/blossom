package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.GenericResponseDto;
import org.blossom.activity.mapper.interfac.IGenericDtoMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GenericDtoMapper implements IGenericDtoMapper {
    @Override
    public GenericResponseDto toDto(String message, Integer id, Map<String, Object> metadata) {
        return GenericResponseDto.builder()
                .responseMessage(message)
                .resourceId(id)
                .metadata(metadata)
                .build();
    }
}
