package org.blossom.social.mapper.interfac;

import org.blossom.social.dto.GenericResponseDto;

import java.util.Map;

public interface IGenericDtoMapper {
    GenericResponseDto toDto(String message, Integer id, Map<String, Object> metadata);
}