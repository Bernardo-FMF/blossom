package org.blossom.social.kafka.outbound.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.blossom.model.KafkaEntity;
import org.blossom.model.KafkaSocialFollowResource;

@Setter
@Getter
@Builder
public class SocialFollow implements KafkaEntity {
    private int initiatingUser;
    private int receivingUser;
    private boolean isMutualFollow;

    @Override
    public KafkaSocialFollowResource mapToResource() {
        return KafkaSocialFollowResource.builder()
                .initiatingUser(initiatingUser)
                .receivingUser(receivingUser)
                .isMutualFollow(isMutualFollow)
                .build();
    }
}
