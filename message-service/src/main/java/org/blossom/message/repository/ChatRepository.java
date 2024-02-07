package org.blossom.message.repository;

import org.blossom.message.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId AND c.lastUpdate IS NOT NULL")
    Page<Chat> findByUserId(@Param("userId") int userId, Pageable page);

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id = :userId AND c.lastUpdate IS NOT NULL")
    List<Chat> findByUserId(@Param("userId") int userId);

    @Transactional
    @Modifying
    @Query("UPDATE Chat c SET c.lastUpdate = instant WHERE c.id = :chatId")
    void updateLastUpdateDate(@Param("chatId") int chatId);
}
