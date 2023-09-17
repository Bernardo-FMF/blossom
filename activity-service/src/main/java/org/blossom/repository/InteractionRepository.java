package org.blossom.repository;

import org.blossom.entity.Interaction;
import org.blossom.enums.InteractionType;
import org.blossom.projection.InteractionCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InteractionRepository extends JpaRepository<Interaction, Integer> {
    void deleteByPostId(String postId);

    boolean existsByUserIdAndPostIdAndInteractionType(int userId, String postId, InteractionType interactionType);

    Optional<Interaction> findByUserIdAndPostIdAndInteractionType(int userId, String postId, InteractionType interactionType);

    Page<Interaction> findByUserIdAndInteractionType(int userId, InteractionType interactionType, Pageable page);

    @Query("SELECT NEW org.blossom.projection.InteractionCountProjection(" +
            "COALESCE(SUM(CASE WHEN i.interactionType = 'LIKE' THEN 1 ELSE 0 END), 0), " +
            "COUNT(CASE WHEN i.interactionType = 'LIKE' AND i.user.id = :userId THEN 1 END) > 0, " +
            "COUNT(CASE WHEN i.interactionType = 'SAVE' AND i.user.id = :userId THEN 1 END) > 0) " +
            "FROM Interaction i " +
            "WHERE i.postId = :postId")
    InteractionCountProjection getInteractionCount(@Param("postId") String postId, @Param("userId") Integer userId);

    @Query("SELECT NEW org.blossom.projection.InteractionCountProjection(" +
            "COALESCE(SUM(CASE WHEN i.interactionType = 'LIKE' THEN 1 ELSE 0 END), 0)) " +
            "FROM Interaction i " +
            "WHERE i.postId = :postId")
    InteractionCountProjection getInteractionCountWithNoUser(@Param("postId") String postId);
}
