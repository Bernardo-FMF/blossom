package org.blossom.feed.mapper;

import org.blossom.feed.entity.LocalPost;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

@Component
public class LocalPostMapper {
    public LocalPost mapToLocalPost(KafkaPostResource source) {
        return LocalPost.builder()
                .id(source.getId())
                .userId(source.getUserId())
                .media(source.getMedia())
                .description(source.getDescription())
                .build();
    }
}
