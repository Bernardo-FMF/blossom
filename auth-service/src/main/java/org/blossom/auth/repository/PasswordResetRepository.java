package org.blossom.auth.repository;

import org.blossom.auth.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, Integer> {
}
