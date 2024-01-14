package org.blossom.activity.mapper.impl;

import org.blossom.activity.dto.PostDto;
import org.blossom.activity.mapper.interfac.IDtoMapper;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

@Component
public class LocalPostDtoMapper implements IDtoMapper<KafkaPostResource, PostDto> {
    @Override
    public PostDto toDto(KafkaPostResource source) {
        return PostDto.builder()
                .userId(source.getUserId())
                .postId(source.getId())
                .build();
    }
}
