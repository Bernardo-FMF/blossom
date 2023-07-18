package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KafkaUserResource extends KafkaResource {
    int id;
    String fullName;
    String userName;
    String imageUrl;
}
