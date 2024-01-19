package org.blossom.feed.mapper;

import org.blossom.feed.entity.FeedEntry;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

@Component
public class FeedEntryMapper {
    public FeedEntry mapToFeedEntry(KafkaPostResource post, int userId) {
        return FeedEntry.builder()
                .key(new FeedEntry.FeedEntryKey(userId, post.getCreatedAt()))
                .postId(post.getId())
                .postCreatorId(post.getUserId())
                .description(post.getDescription())
                .build();
    }
}
