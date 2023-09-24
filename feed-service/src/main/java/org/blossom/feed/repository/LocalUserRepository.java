package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalUser;
import org.springframework.data.cassandra.repository.CassandraRepository;

public interface LocalUserRepository extends CassandraRepository<LocalUser, Integer> {
}
