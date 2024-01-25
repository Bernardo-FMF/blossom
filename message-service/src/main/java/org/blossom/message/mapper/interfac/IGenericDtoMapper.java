package org.blossom.message.mapper.interfac;

import org.blossom.message.dto.GenericResponseDto;

import java.util.Map;

public interface IGenericDtoMapper {
    GenericResponseDto toDto(String message, Integer id, Map<String, Object> metadata);
}