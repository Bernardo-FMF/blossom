package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalUserPostCount;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface LocalUserPostCountRepository extends CassandraRepository<LocalUserPostCount, Integer> {
}
