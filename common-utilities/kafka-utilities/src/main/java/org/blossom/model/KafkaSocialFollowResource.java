package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KafkaSocialFollowResource extends KafkaResource {
    private int initiatingUser;
    private int receivingUser;
    boolean isMutualFollow;
}
