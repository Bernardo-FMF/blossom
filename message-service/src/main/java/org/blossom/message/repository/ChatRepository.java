package org.blossom.message.repository;

import org.blossom.message.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId")
    Page<Chat> findByUserId(@Param("userId") int userId, Pageable page);

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId")
    List<Chat> findByUserId(@Param("userId") int userId);
}
