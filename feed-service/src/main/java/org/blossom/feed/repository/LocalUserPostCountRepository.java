package org.blossom.feed.repository;

import org.blossom.feed.entity.LocalUserPostCount;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocalUserPostCountRepository extends CassandraRepository<LocalUserPostCount, Integer> {
    @Query("update \"Blossom_Local_User_Post_Count\" set postCount = postCount + 1 where userId = :userId")
    void incrementCount(@Param("userId") Integer userId);

    @Query("update \"Blossom_Local_User_Post_Count\" set postCount = postCount - 1 where userId = :userId")
    void decrementCount(@Param("userId") Integer userId);

    @Query("update \"Blossom_Local_User_Post_Count\" set postCount = postCount + 0 where userId = :userId")
    void createCount(@Param("userId") Integer userId);
}
