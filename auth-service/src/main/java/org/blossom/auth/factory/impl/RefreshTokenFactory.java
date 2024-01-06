package org.blossom.auth.factory.impl;

import org.blossom.auth.entity.RefreshToken;
import org.blossom.auth.entity.User;
import org.blossom.auth.factory.interfac.IEntityFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class RefreshTokenFactory implements IEntityFactory<RefreshToken, User> {
    @Value("${jwt.refresh.duration}")
    private long refreshDuration;

    public RefreshToken buildEntity(User data) {
        return RefreshToken.builder()
                .user(data)
                .token(generateUniqueToken())
                .expirationDate(Instant.now().plusMillis(refreshDuration))
                .build();
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
}
