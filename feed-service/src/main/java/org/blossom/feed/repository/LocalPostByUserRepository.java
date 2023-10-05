package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalPostByUser;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface LocalPostByUserRepository extends CassandraRepository<LocalPostByUser, Integer> {
}