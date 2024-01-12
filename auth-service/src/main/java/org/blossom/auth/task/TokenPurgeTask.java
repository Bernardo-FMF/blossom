package org.blossom.auth.task;

import org.blossom.auth.repository.PasswordResetRepository;
import org.blossom.auth.repository.RefreshTokenRepository;
import org.blossom.auth.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
public class TokenPurgeTask {
    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 1 * * *")
    public void purgeExpiredTokens() {
        Instant now = Instant.now();

        tokenRepository.deleteAllExpiredSince(now);
        passwordResetRepository.deleteAllExpiredSince(now);
        refreshTokenRepository.deleteAllExpiredSince(now);
    }
}
