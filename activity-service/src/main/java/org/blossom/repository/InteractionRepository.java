package org.blossom.repository;

import org.blossom.entity.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractionRepository extends JpaRepository<Interaction, Integer> {
}
