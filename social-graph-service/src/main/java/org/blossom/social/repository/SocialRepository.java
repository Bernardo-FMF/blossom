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

    @Query(value = "MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser) " +
            "WHERE followed.userId = $followedId " +
            "RETURN follower SKIP $skip LIMIT $limit",
            countQuery = "MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser)" +
                    "WHERE followed.userId = $followedId " +
                    "RETURN DISTINCT COUNT(follower)")
    Page<GraphUser> findFollowers(@Param("followedId") Integer followedId, Pageable pageable);

    @Query("MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser) " +
            "WHERE followed.userId = $followedId " +
            "RETURN follower.userId")
    List<Integer> findFollowersUnpaged(@Param("followedId") Integer followedId);

    @Query(value = "MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser) " +
            "WHERE follower.userId = $followerId " +
            "RETURN followed SKIP $skip LIMIT $limit",
            countQuery = "MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser)" +
                    "WHERE follower.userId = $followerId " +
                    "RETURN DISTINCT COUNT(followed)")
    Page<GraphUser> findFollowing(@Param("followerId") Integer followerId, Pageable pageable);

    @Query(value = "MATCH (self:GraphUser)-[:FOLLOWS]->(following:GraphUser)-[:FOLLOWS]->(recommended:GraphUser) " +
            "WHERE self.userId = $user AND NOT (self)-[:FOLLOWS]->(recommended) " +
            "RETURN DISTINCT recommended SKIP $skip LIMIT $limit",
            countQuery = "MATCH (self:GraphUser)-[:FOLLOWS]->(following:GraphUser)-[:FOLLOWS]->(recommended:GraphUser) " +
                    "WHERE self.userId = $user AND NOT (self)-[:FOLLOWS]->(recommended) " +
                    "RETURN DISTINCT COUNT(recommended)")
    Page<GraphUser> findRecommendations(@Param("user") Integer user, Pageable pageable);

    @Query("MATCH (follower:GraphUser)-[r:FOLLOWS]->(followed:GraphUser) "
            + "WITH follower, COUNT(r) AS numFollowers "
            + "ORDER BY numFollowers DESC "
            + "LIMIT $limit "
            + "RETURN followed.userId")
    List<Integer> findMostFollowedUsers(@Param("limit") Integer limit);
}
