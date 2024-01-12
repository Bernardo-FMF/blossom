package org.blossom.auth.repository;

import org.blossom.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    void deleteByUserId(Integer id);

    Optional<RefreshToken> findByTokenAndUserId(String token, Integer userId);

    @Modifying
    @Query("delete from RefreshToken rt where rt.expirationDate <= :now")
    void deleteAllExpiredSince(@Param("now") Instant now);
}
