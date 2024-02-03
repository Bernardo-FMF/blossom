package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;

@Getter
@Setter
@Builder
public class KafkaPostResource extends KafkaResource {
    private String id;
    private int userId;
    private String[] media;
    private String[] hashtags;
    private String description;
    private Instant createdAt;

    @Override
    public String toString() {
        return "KafkaPostResource(id=" + this.getId() +
                ", userId=" + this.getUserId() +
                ", media=" + Arrays.deepToString(this.getMedia()) +
                ", hashtags=" + Arrays.deepToString(this.getHashtags()) +
                ", description=" + this.getDescription() +
                ", createdAt=" + this.getCreatedAt() + ")";
    }
}
