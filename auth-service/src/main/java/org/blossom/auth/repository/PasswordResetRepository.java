package org.blossom.auth.repository;

import org.blossom.auth.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {
    @Modifying
    @Query("delete from PasswordReset pr where pr.expirationDate <= :now")
    void deleteAllExpiredSince(@Param("now") Instant now);
}
