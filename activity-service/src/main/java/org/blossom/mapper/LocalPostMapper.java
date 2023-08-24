package org.blossom.mapper;

import org.blossom.kafka.model.LocalPost;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

@Component
public class LocalPostMapper {
    public LocalPost mapToLocalPost(KafkaPostResource source) {
        return LocalPost.builder()
                .userId(source.getUserId())
                .postId(source.getId())
                .build();
    }
}
