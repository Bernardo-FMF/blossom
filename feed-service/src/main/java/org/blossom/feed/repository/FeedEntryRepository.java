package org.blossom.feed.repository;

import org.blossom.feed.entity.FeedEntry;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FeedEntryRepository extends CassandraRepository<FeedEntry, Integer> {
    Slice<FeedEntry> findByUserId(int userId, Pageable pageable);
    long countByUserId(int userId);
    void deleteByPostId(String postId);
}
