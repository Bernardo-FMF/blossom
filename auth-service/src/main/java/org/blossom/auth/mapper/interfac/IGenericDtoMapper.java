package org.blossom.auth.mapper.interfac;

import org.blossom.auth.dto.GenericResponseDto;

import java.util.Map;

public interface IGenericDtoMapper {
    GenericResponseDto toDto(String message, Integer id, Map<String, Object> metadata);
}
