package org.blossom.post.mapper.interfac;


import org.blossom.post.dto.GenericResponseDto;

import java.util.Map;

public interface IGenericDtoMapper {
    GenericResponseDto toDto(String message, String id, Map<String, Object> metadata);
}
