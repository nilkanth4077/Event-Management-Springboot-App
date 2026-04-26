package com.event_management.repositories;

import com.event_management.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByRole(String admin);

    long countByRole(String role);
}
