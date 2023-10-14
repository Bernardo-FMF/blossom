package org.blossom.message.repository;

import org.blossom.message.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    @Query("SELECT bcm FROM Blossom_Chat_Message bcm WHERE bcm.chat.id = :chatId")
    Page<Message> findByChatId(@Param("chatId") int chatId, Pageable page);
}
