package org.blossom.feed.repository;

import org.blossom.feed.entity.InvertedFeedEntry;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface InvertedFeedEntryRepository extends CassandraRepository<InvertedFeedEntry, String> {
    List<InvertedFeedEntry> findByPostId(String postId);
}
