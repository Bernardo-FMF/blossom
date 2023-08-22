package org.blossom.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.blossom.enums.InteractionType;

@Builder
@Getter
@Setter
@Entity
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "post_id")
    private String postId;

    @Enumerated(EnumType.STRING)
    private InteractionType interactionType;
}
