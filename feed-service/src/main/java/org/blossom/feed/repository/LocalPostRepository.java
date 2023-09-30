package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalPost;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface LocalPostRepository extends CassandraRepository<LocalPost, String> {
    Slice<LocalPost> findByIdIn(List<String> postIds, Pageable pageable);
}
