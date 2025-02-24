package org.example.blps_lab1.courseSignUp.repository;

import org.example.blps_lab1.courseSignUp.models.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
}
