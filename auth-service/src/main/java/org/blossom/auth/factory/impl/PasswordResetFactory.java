package org.blossom.auth.factory.impl;

import org.blossom.auth.entity.PasswordReset;
import org.blossom.auth.entity.User;
import org.blossom.auth.factory.interfac.IEntityFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PasswordResetFactory implements IEntityFactory<PasswordReset, User> {
    @Override
    public PasswordReset buildEntity(User data) {
        return PasswordReset.builder()
                .user(data)
                .id(data.getId())
                .token(generateUniqueToken())
                .expirationDate(LocalDateTime.now().plusHours(1))
                .build();
    }

    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
}
