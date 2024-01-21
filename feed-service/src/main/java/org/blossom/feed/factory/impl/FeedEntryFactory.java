package org.blossom.feed.factory.impl;

import org.blossom.feed.entity.FeedEntry;
import org.blossom.feed.factory.interfac.ICompoundEntityFactory;
import org.blossom.model.KafkaPostResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedEntryFactory implements ICompoundEntityFactory<FeedEntry, KafkaPostResource, Integer> {
    @Override
    public FeedEntry buildEntity(KafkaPostResource data, Integer data2) {
        return FeedEntry.builder()
                .key(new FeedEntry.FeedEntryKey(data2, data.getCreatedAt()))
                .postId(data.getId())
                .postCreatorId(data.getUserId())
                .media(List.of(data.getMedia()))
                .hashtags(List.of(data.getHashtags()))
                .description(data.getDescription())
                .build();
    }
}
