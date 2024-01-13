package org.blossom.social.factory.impl;

import org.blossom.social.dto.SocialRelationDto;
import org.blossom.social.kafka.outbound.model.SocialFollow;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SocialFollowFactory {
    public SocialFollow buildEntity(SocialRelationDto data, int userId, boolean mutualFollow) {
        return SocialFollow.builder()
                .initiatingUser(userId)
                .receivingUser(data.getReceivingUser())
                .isMutualFollow(mutualFollow)
                .createdAt(Instant.now())
                .build();
    }
}
