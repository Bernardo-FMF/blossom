package org.blossom.feed.mapper;

import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.entity.InvertedFeedEntry;
import org.springframework.stereotype.Component;

@Component
public class FeedEntryMapper {
    public FeedEntry mapToFeedEntry(String postId, int userId) {
        return FeedEntry.builder()
                .key(new FeedEntry.FeedEntryKey(userId, postId))
                .build();
    }

    public InvertedFeedEntry mapToInvertedFeedEntry(String postId, int userId) {
        return InvertedFeedEntry.builder()
                .postId(postId)
                .userId(userId)
                .build();
    }
}
