package org.blossom.feed.mapper;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import org.blossom.feed.entity.FeedEntry;
import org.springframework.stereotype.Component;

@Component
public class FeedEntryMapper {
    public FeedEntry mapToFeedEntry(String postId, int userId) {
        return FeedEntry.builder()
                .id(Uuids.timeBased())
                .postId(postId)
                .userId(userId)
                .build();
    }
}
