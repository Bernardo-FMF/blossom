package org.blossom.auth.repository;

import org.blossom.auth.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer> {
    @Modifying
    @Query("delete from VerificationToken vt where vt.expirationDate <= :now")
    void deleteAllExpiredSince(@Param("now") Instant now);

    @Modifying
    @Query("delete from VerificationToken vt where vt.user.id = :id")
    void deleteByUserId(@Param("id") Integer id);
}
