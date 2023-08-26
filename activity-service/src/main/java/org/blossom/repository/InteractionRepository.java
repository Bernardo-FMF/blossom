package org.blossom.repository;

import org.blossom.entity.Interaction;
import org.blossom.enums.InteractionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractionRepository extends JpaRepository<Interaction, Integer> {
    void deleteByPostId(String postId);

    boolean existsByUserIdAndPostIdAndInteractionType(int userId, String postId, InteractionType interactionType);

    Page<Interaction> findByUserIdAndInteractionType(int userId, InteractionType interactionType, Pageable page);
}
