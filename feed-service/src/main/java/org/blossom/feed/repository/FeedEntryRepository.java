package org.blossom.feed.repository;

import org.blossom.feed.entity.FeedEntry;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface FeedEntryRepository extends CassandraRepository<FeedEntry, Integer> {
    Slice<FeedEntry> findByKeyUserId(int userId, Pageable pageable);

    long countByKeyUserId(int userId);

    @AllowFiltering
    List<FeedEntry> findByPostIdIn(List<String> postIds);
}
