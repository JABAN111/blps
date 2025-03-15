package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.authorization.models.User;
import org.example.blps_lab1.courseSignUp.models.Module;
import org.example.blps_lab1.courseSignUp.models.UserModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserModuleProgressRepository extends JpaRepository<UserModuleProgress, Long> {
    Optional<UserModuleProgress> findByUserAndModule(User user, Module module);
}
