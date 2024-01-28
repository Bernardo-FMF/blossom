package org.blossom.message.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.blossom.model.KafkaEntity;
import org.blossom.model.KafkaMessageResource;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Blossom_Chat_Message")
public class Message implements KafkaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Override
    public KafkaMessageResource mapToResource() {
        return KafkaMessageResource.builder()
                .id(id)
                .senderId(sender.getId())
                .recipientsIds(chat.getParticipants().stream().map(User::getId).filter(participantId -> participantId != id).toArray(Integer[]::new))
                .chatId(chat.getId())
                .content(content)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .isDeleted(isDeleted)
                .build();
    }
}
