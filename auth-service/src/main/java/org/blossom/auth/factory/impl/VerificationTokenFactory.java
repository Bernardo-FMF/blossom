package org.blossom.auth.factory.impl;

import org.blossom.auth.entity.User;
import org.blossom.auth.entity.VerificationToken;
import org.blossom.auth.factory.interfac.IEntityFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class VerificationTokenFactory implements IEntityFactory<VerificationToken, User> {
    @Override
    public VerificationToken buildEntity(User data) {
        return VerificationToken.builder()
                .user(data)
                .token(generateUniqueToken())
                .expirationDate(Instant.now().plus(Duration.ofDays(7)))
                .build();
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
}
