package org.blossom.entity;

import jakarta.persistence.*;
import lombok.*;
import org.blossom.enums.InteractionType;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "Blossom_Interaction")
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private LocalUser user;

    @Column(name = "post_id")
    private String postId;

    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
