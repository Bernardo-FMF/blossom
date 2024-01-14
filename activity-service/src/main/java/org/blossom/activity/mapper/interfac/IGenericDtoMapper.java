package org.blossom.activity.mapper.interfac;

import org.blossom.activity.dto.GenericResponseDto;

import java.util.Map;

public interface IGenericDtoMapper {
    GenericResponseDto toDto(String message, Integer id, Map<String, Object> metadata);
}