package org.blossom.auth.repository;

import org.blossom.auth.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<PasswordReset, Integer> {
}
