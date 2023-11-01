package org.blossom.activity.repository;

import org.blossom.activity.entity.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalUserRepository extends JpaRepository<LocalUser, Integer> {
}
