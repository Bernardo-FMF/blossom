package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KafkaPostResource extends KafkaResource {
    private String id;
    private int userId;
    private String[] media;
    private String description;
}
