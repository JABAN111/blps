package org.example.blps_lab1.adapters.db.auth;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.ports.db.UserDatabase;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("stage")
public interface UserRepository extends JpaRepository<User, Long>, UserDatabase {
    Optional<User> findByEmail(String email);
}
