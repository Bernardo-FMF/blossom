package org.blossom.feed.mapper;

import org.blossom.feed.entity.FeedEntry;
import org.springframework.stereotype.Component;

@Component
public class FeedEntryMapper {
    public FeedEntry mapToFeedEntry(String postId, int userId) {
        return FeedEntry.builder()
                .postId(postId)
                .userId(userId)
                .build();
    }
}
