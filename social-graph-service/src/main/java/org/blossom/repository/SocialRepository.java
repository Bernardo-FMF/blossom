package org.blossom.repository;

import org.blossom.entity.GraphUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SocialRepository extends Neo4jRepository<GraphUser, Integer> {
    @Query("MATCH (follower:LocalUser), (followed:LocalUser) " +
            "WHERE follower.userId = $followerId AND followed.userId = $followedId " +
            "CREATE (follower)-[:FOLLOWS]->(followed)")
    void createFollowerRelationship(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Query("MATCH (follower:LocalUser { userId: $followerId }) " +
            "MATCH (followed:LocalUser { userId: $followedId }) " +
            "RETURN EXISTS((follower)-[:FOLLOWS]->(followed))")
    boolean existsRelationshipBetweenUsers(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Query("MATCH (follower:LocalUser)-[r:FOLLOWS]->(followed:LocalUser) " +
            "WHERE follower.userId = $followerId AND followed.userId = $followedId " +
            "DELETE r")
    void deleteFollowerRelationship(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Query("MATCH (follower:LocalUser)-[r:FOLLOWS]->(followed:LocalUser)" +
            "WHERE followed.userId = $followedId" +
            "RETURN follower.userId")
    List<Integer> findFollowers(@Param("followedId") Integer followedId);

    @Query(value = "MATCH (self:LocalUser)-[:FOLLOWS]->(following:LocalUser)-[:FOLLOWS]->(recommended:LocalUser) " +
            "WHERE self.userId = $user AND NOT (self)-[:FOLLOWS]->(recommended) " +
            "RETURN DISTINCT recommended.userId",
            countQuery = "MATCH (self:LocalUser)-[:FOLLOWS]->(following:LocalUser)-[:FOLLOWS]->(recommended:LocalUser) " +
                    "WHERE self.userId = $user AND NOT (self)-[:FOLLOWS]->(recommended) " +
                    "RETURN DISTINCT COUNT(recommended)")
    Page<Integer> findRecommendations(@Param("user") Integer user, Pageable pageable);
}
