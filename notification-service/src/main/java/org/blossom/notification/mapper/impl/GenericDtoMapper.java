package org.blossom.notification.mapper.impl;

import org.blossom.notification.dto.GenericResponseDto;
import org.blossom.notification.mapper.interfac.IGenericDtoMapper;
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
