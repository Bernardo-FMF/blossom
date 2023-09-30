package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalUserPosts;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface LocalUserPostsRepository extends CassandraRepository<LocalUserPosts, Integer> {
}
