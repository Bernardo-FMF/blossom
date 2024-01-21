package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

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
}
