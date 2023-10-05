package org.blossom.feed.repository;

import org.blossom.feed.entity.FeedEntry;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FeedEntryRepository extends CassandraRepository<FeedEntry, Integer> {
    Slice<FeedEntry> findByKeyUserId(int userId, Pageable pageable);
    long countByKeyUserId(int userId);

    void deleteByKeyUserIdAndKeyPostId(int userId, String postId);
}
