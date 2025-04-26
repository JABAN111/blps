package org.example.blps_lab1.adapters.db.course;

import org.example.blps_lab1.core.domain.auth.User;
import org.example.blps_lab1.core.domain.course.Module;
import org.example.blps_lab1.core.domain.course.UserModuleProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserModuleProgressRepository extends JpaRepository<UserModuleProgress, Long> {
    Optional<UserModuleProgress> findByUserAndModule(User user, Module module);
}
