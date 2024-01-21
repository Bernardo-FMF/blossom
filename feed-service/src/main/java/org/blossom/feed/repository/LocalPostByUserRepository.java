package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalPostByUser;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Set;

public interface LocalPostByUserRepository extends CassandraRepository<LocalPostByUser, Integer> {
    @AllowFiltering
    List<LocalPostByUser> findByPostIdIn(List<String> postIds);

    Slice<LocalPostByUser> findByKeyUserIdIn(Set<Integer> integers, Pageable page);

    List<LocalPostByUser> findByKeyUserIdIn(List<Integer> integers);
}