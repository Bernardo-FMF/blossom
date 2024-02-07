package org.blossom.message.repository;

import jakarta.transaction.Transactional;
import org.blossom.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId")
    Page<Message> findByChatId(@Param("chatId") int chatId, Pageable page);

    @Transactional
    @Modifying
    @Query("DELETE FROM Message m WHERE m.chat.id = :chatId")
    void deleteByChatId(@Param("chatId") int chatId);

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.sender = NULL WHERE m.chat.id = :chatId AND m.sender.id = :userId")
    void decoupleUserFromChat(@Param("chatId") int chatId, @Param("userId") int userId);
}
