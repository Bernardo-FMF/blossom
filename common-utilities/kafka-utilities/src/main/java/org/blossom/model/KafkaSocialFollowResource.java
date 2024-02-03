package org.blossom.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class KafkaSocialFollowResource extends KafkaResource {
    private int initiatingUser;
    private int receivingUser;
    private boolean isMutualFollow;
    private Instant createdAt;

    @Override
    public String toString() {
        return "KafkaSocialFollowResource(initiatingUser=" + this.getInitiatingUser() +
                ", receivingUser=" + this.getReceivingUser() +
                ", isMutualFollow=" + this.isMutualFollow() +
                ", createdAt=" + this.getCreatedAt() + ")";
    }
}
