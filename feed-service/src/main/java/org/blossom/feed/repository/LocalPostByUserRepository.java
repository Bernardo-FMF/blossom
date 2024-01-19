package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalPostByUser;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface LocalPostByUserRepository extends CassandraRepository<LocalPostByUser, Integer> {
    @AllowFiltering
    List<LocalPostByUser> findByPostIdIn(List<String> postIds);
}