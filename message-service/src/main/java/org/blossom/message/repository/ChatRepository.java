package org.blossom.message.repository;

import org.blossom.message.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
    @Query("SELECT bc FROM Blossom_Chat bc WHERE bc.owner.id = :userId")
    Page<Chat> findByUserId(@Param("userId") int userId, Pageable page);

    @Transactional
    @Modifying
    @Query("UPDATE Blossom_Chat c SET c.lastUpdate = CURRENT_TIMESTAMP WHERE c.id = :chatId")
    void updateLastUpdateDate(@Param("chatId") int chatId);
}
