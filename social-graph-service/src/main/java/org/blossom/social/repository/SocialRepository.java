package org.blossom.social.repository;

import org.blossom.social.entity.GraphUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SocialRepository extends Neo4jRepository<GraphUser, Integer> {
    @Query("MATCH (follower:GraphUser), (followed:GraphUser) " +
            "WHERE follower.userId = $followerId AND followed.userId = $followedId " +
            "CREATE (follower)-[:FOLLOWS]->(followed)")
    void createFollowerRelationship(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Query("MATCH (follower:GraphUser { userId: $followerId }) " +
            "MATCH (followed:GraphUser { userId: $followedId }) " +
            "RETURN EXISTS((follower)-[:FOLLOWS]->(followed))")
    boolean existsRelationshipBetweenUsers(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Query("MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser) " +
            "WHERE follower.userId = $followerId AND followed.userId = $followedId " +
            "DELETE r")
    void deleteFollowerRelationship(@Param("followerId") Integer followerId, @Param("followedId") Integer followedId);

    @Query("MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser) " +
            "WHERE followed.userId = $followedId " +
            "RETURN follower")
    List<GraphUser> findFollowers(@Param("followedId") Integer followedId);

    @Query(value = "MATCH (self:GraphUser)-[:FOLLOWS]->(following:GraphUser)-[:FOLLOWS]->(recommended:GraphUser) " +
            "WHERE self.userId = $user AND NOT (self)-[:FOLLOWS]->(recommended) " +
            "RETURN DISTINCT recommended",
            countQuery = "MATCH (self:GraphUser)-[:FOLLOWS]->(following:GraphUser)-[:FOLLOWS]->(recommended:GraphUser) " +
                    "WHERE self.userId = $user AND NOT (self)-[:FOLLOWS]->(recommended) " +
                    "RETURN DISTINCT COUNT(recommended)")
    Page<GraphUser> findRecommendations(@Param("user") Integer user, Pageable pageable);
}
