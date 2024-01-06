package org.blossom.auth.repository;

import org.blossom.auth.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsernameAndVerifiedIsTrue(String username);
    Optional<User> findByIdAndVerifiedIsTrue(Integer id);
    Optional<User> findByEmailAndVerifiedIsTrue(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findByUsernameContainingIgnoreCaseAndVerifiedIsTrueOrFullNameContainingIgnoreCaseAndVerifiedIsTrue(String usernameQuery, String fullNameQuery, Pageable pageable);
}
