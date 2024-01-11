package org.blossom.activity.mapper;

import org.blossom.activity.dto.PostDto;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

@Component
public class LocalPostMapper {
    public PostDto mapToLocalPost(KafkaPostResource source) {
        return PostDto.builder()
                .userId(source.getUserId())
                .postId(source.getId())
                .build();
    }
}
