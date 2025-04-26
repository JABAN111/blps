package org.example.blps_lab1.core.ports.db;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.auth.UserXml;

import java.util.Optional;

public interface UserDatabase {
    UserXml save(UserXml user);
    Optional<UserXml> findByEmail(String email);
    Optional<UserXml> findById(Long userId);
}
