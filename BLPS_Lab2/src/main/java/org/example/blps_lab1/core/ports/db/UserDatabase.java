package org.example.blps_lab1.core.ports.db;

import org.example.blps_lab1.core.domain.auth.User;

import java.util.Optional;

public interface UserDatabase {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long userId);
}
