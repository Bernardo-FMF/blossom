package org.blossom.notification.mapper.interfac;

import org.blossom.notification.dto.GenericResponseDto;

import java.util.Map;

public interface IGenericDtoMapper {
    GenericResponseDto toDto(String message, String id, Map<String, Object> metadata);
}