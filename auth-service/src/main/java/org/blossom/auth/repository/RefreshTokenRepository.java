package org.blossom.auth.repository;

import jakarta.transaction.Transactional;
import org.blossom.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    @Modifying
    @Query("delete from RefreshToken rt where rt.user.id = :id")
    void deleteByUserId(@Param("id") Integer id);

    Optional<RefreshToken> findByTokenAndUserId(String token, Integer userId);

    @Transactional
    @Modifying
    @Query("delete from RefreshToken rt where rt.expirationDate <= :date")
    void deleteAllExpiredSince(@Param("date") Instant date);
}
