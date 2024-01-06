package org.blossom.auth.repository;

import org.blossom.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    void deleteByUserId(Integer id);
    Optional<RefreshToken> findByTokenAndUserId(String token, Integer userId);
}
